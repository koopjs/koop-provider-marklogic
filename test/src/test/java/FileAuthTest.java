import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

/**
 * Verifies that "file auth" works - i.e. the client sends a username/password, which is verified against the
 * ./config/fake-user-store.json file. If valid, a token is returned, which is then included as the Authorization header
 * on subsequent requests.
 * <p>
 * Depends on "npm run start-file-auth" running.
 */
public class FileAuthTest {

    @BeforeClass
    public static void connectToFileAuthKoop() {
        RestAssured.port = 8091;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void validUsernameAndPassword() {
        ObjectNode response = RestAssured
            .given()
            .when()
            .get("/marklogic/tokens?username=koop-auth-direct-file-user&password=test")
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
            .get("/marklogic/tokens?username=koop-auth-direct-file-user&password=wrong")
            .then()
            .statusCode(200)
            .body("error.message", is("Unable to generate token."))
            .body("error.details[0]", is("Invalid username or password."));
    }

    @Test
    public void invalidAccessToken() {
        RestAssured
            .given()
            .when().header("Authorization", "not a valid token")
            .get("/marklogic/GDeltGKG/FeatureServer")
            .then()
            .statusCode(200)
            // Oddly, the message is "Token Required" even though a token was provided - it just wasn't valid.
            // This seems like something to improve.
            .body("error.message", is("Token Required"));
    }
}
