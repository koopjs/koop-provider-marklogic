import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

/**
 * Verifies the "auth-marklogic-basic-header" authorization plugin.
 */
public class BasicAuthHeaderTest extends AbstractFeatureServiceTest {

    @Before
    public void connectToBasicHeaderKoop() {
        RestAssured.port = 8092;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void validUsernameAndPassword() {
        new RestAssuredHelper()
            .withBasicHeader("test-geo-data-services-writer", "test-geo-data-services-writer")
            .get(request2path("featureService.json"))
            // Just make sure at least one field is correct, that's good enough to verify that we logged in successfully.
            .body("currentVersion", is(10.51f));
    }

    @Test
    public void invalidUsernameAndPassword() {
        new RestAssuredHelper().withBasicHeader("bad", "login")
            .get(request2path("featureService.json"), 200)
            // While https://koopjs.github.io/docs/development/authorization#authorize-function-authorizereq--promise
            // states that the 'authorize' function in a plugin should return a 401, Koop seems to then convert this
            // into a 200 and instead included the error in the response body. And that error always has a message of
            // "Token Required", even though that's not really appropriate here.
            .body("error.code", is(499))
            .body("error.message", is("Token Required"));
    }

    @Test
    public void noHeaderFound() {
        new RestAssuredHelper()
            .get(request2path("featureService.json"), 200)
            .body("error.code", is(499))
            .body("error.message", is("Token Required"));
    }

    @Test
    public void invokeTokensEndpoint() {
        new RestAssuredHelper()
            // It appears that if an auth plugin returns an error with a code other than 401, then that error will be
            // returned to the client. A 400 is expected here since the "authenticate" function is not expected to
            // work for this plugin since it does not support generating tokens.
            .get("/marklogic/tokens?username=test-geo-data-services-writer&password=test-geo-data-services-writer", 400)
            .body("error", is("Token generation not supported"));
    }

    /**
     * Intent of this test is to make sure that a MarkLogic client instance isn't cached and reused when the user
     * submits a request with the same username but an invalid password.
     */
    @Test
    public void validPasswordAndThenInvalidPassword() {
        final String user = "test-geo-data-services-writer";
        final String validPassword = "test-geo-data-services-writer";
        final String invalidPassword = "bad";

        new RestAssuredHelper()
            .withBasicHeader(user, validPassword)
            .get(request2path("featureService.json"))
            .body("currentVersion", is(10.51f));

        new RestAssuredHelper()
            .withBasicHeader(user, invalidPassword)
            .get(request2path("featureService.json"))
            .body("error.code", is(499))
            .body("error.message", is("Token Required"));
    }

    @Test
    public void invalidMarkLogicPort() {
        RestAssured.port = 8094;
        new RestAssuredHelper()
            .withBasicHeader("test-geo-data-services-writer", "test-geo-data-services-writer")
            .get(request2path("featureService.json"), 500)
            // When a 500 is returned by a Koop provider, the Koop server will always have an error message of
            // "Internal Server Error".
            .body("error", is("Internal Server Error"));
    }
}
