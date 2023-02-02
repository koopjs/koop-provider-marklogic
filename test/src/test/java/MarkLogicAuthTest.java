import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

/**
 * Verifies that "MarkLogic auth" works - i.e. the client sends a MarkLogic username/password, and that's verified
 * against MarkLogic, and then our provider returns an access token.
 *
 * Depends on "npm run start-ml-auth" running.
 */
public class MarkLogicAuthTest {

    @BeforeClass
    public static void connectToMarkLogicAuthKoop() {
        RestAssured.port = 8092;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void validUsernameAndPassword() {
        ObjectNode response = RestAssured
            .given()
            .when()
            .get("/marklogic/tokens?username=test-geo-data-services-writer&password=test-geo-data-services-writer")
            .then()
            .statusCode(200)
            .extract().body().as(ObjectNode.class);
        String accessToken = response.get("token").asText();

        RestAssured
            .given()
            .when().header("Authorization", accessToken)
            .get("/marklogic/GDeltGKG/FeatureServer")
            .then()
            .statusCode(200)
            .body("serviceDescription", is("GDelt GKG data. Georesolution values are 1=country, 2=US State / non-US ADM1, 3=city/landmark."));
    }

    @Test
    public void invalidPassword() {
        RestAssured
            .given()
            .when()
            .get("/marklogic/tokens?username=test-geo-data-services-writer&password=wrong")
            .then()
            .statusCode(200)
            .body("error.message", is("Unable to generate token."))
            .body("error.details[0]", is("Invalid username or password."));
    }

    @Test
    public void invalidToken() {
        RestAssured
            .given()
            .when().header("Authorization", "not a valid token")
            .get("/marklogic/GDeltGKG/FeatureServer")
            .then()
            // While none/file auth return a 200, our MarkLogic auth returns a 403. Not sure if this is expected to
            // differ from the other two auth strategies.
            .statusCode(403)
            .body("error", is("jwt malformed"));
    }
}
