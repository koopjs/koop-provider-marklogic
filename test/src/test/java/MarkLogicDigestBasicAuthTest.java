import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

/**
 * Verifies that "MarkLogic auth" works - i.e. the client sends a MarkLogic username/password, and that's verified
 * against MarkLogic, and then our provider returns an access token.
 */
public class MarkLogicDigestBasicAuthTest extends AbstractFeatureServiceTest {

    @Before
    public void connectToDigestBasicAuthKoop() {
        RestAssured.port = 8091;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void validUsernameAndPassword() {
        String accessToken = getTokenAsValidUser();
        RestAssured
            .given()
            .when().header("Authorization", accessToken)
            .get(basePath("GDeltGKG/FeatureServer"))
            .then()
            .statusCode(200)
            .body("currentVersion", is(10.51f));
    }

    /**
     * Per https://developers.arcgis.com/rest/users-groups-and-items/common-parameters.htm, the token is a common
     * parameter that can be sent on the querystring instead of in an authorization header.
     */
    @Test
    public void tokenSentOnQuerystring() {
        String accessToken = getTokenAsValidUser();
        RestAssured
            .given()
            .get(basePath("GDeltGKG/FeatureServer?token=" + accessToken))
            .then()
            .statusCode(200)
            .body("currentVersion", is(10.51f));
    }

    @Test
    public void invalidPassword() {
        authenticateAsUser("test-geo-data-services-writer", "wrong")
            .statusCode(200)
            .body("error.message", is("Unable to generate token."))
            .body("error.details[0]", is("Invalid username or password."));
    }

    @Test
    public void invalidToken() {
        RestAssured
            .given()
            .when().header("Authorization", "not a valid token")
            .get(basePath("GDeltGKG/FeatureServer"))
            .then()
            // This used to expect a 403, but that isn't in line with what the authorization spec states:
            // https://koopjs.github.io/docs/development/authorization#authorize-function-authorizereq--promise .
            // Additionally, the koop-auth-direct-file returns a 401 for this same scenario:
            // https://github.com/koopjs/koop-auth-direct-file/blob/master/src/index.js#L108 .
            // Koop then turns that 401 into a 200 and captures the error in the body of the response. And Koop is
            // what creates the body containing a 499 and the message asserted on below.
            .statusCode(200)
            .body("error.code", is(499))
            .body("error.message", is("Token Required"));
    }

    @Test
    public void missingToken() {
        RestAssured
            .given()
            .get(basePath("GDeltGKG/FeatureServer"))
            .then()
            .statusCode(200)
            .body("error.code", is(499))
            .body("error.message", is("Token Required"));
    }

    @Test
    public void invalidMarkLogicPort() {
        RestAssured.port = 8093;
        authenticateAsUser("test-geo-data-services-writer", "test-geo-data-services-writer")
            .statusCode(500)
            // When a 500 is returned by a Koop provider, the Koop server will always have an error message of
            // "Internal Server Error".
            .body("error", is("Internal Server Error"));
    }

    private String getTokenAsValidUser() {
        ObjectNode response =
            authenticateAsUser("test-geo-data-services-writer", "test-geo-data-services-writer")
                .statusCode(200)
                .extract().body().as(ObjectNode.class);
        return response.get("token").asText();
    }

    private ValidatableResponse authenticateAsUser(String username, String password) {
        return RestAssured.given().when()
                   .get(String.format("/marklogic/tokens?username=%s&password=%s", username, password))
                   .then();
    }
}
