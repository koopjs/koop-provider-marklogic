import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.codec.binary.Base64;

/**
 * This is a "helper" currently for removing a bunch of duplication across hundreds of tests - the duplicated code is
 * now in this class's "get" method. The scope of this class can be expanded as needed.
 */
public class RestAssuredHelper {

    private RequestSpecification spec;

    public RestAssuredHelper() {
        spec = RestAssured.given();
    }

    public RestAssuredHelper(RequestSpecification spec) {
        this.spec = spec;
    }

    public RestAssuredHelper withBasicHeader(String username, String password) {
        spec.header("authorization", buildBasicHeader(username, password));
        return this;
    }

    private String buildBasicHeader(String username, String password) {
        String str = username + ":" + password;
        String encoded = new String(Base64.encodeBase64(str.getBytes()));
        return "Basic " + encoded;
    }

    public RequestSpecification pathParam(String name, Object value) {
        return spec.pathParam(name, value);
    }

    public ValidatableResponse get(String path) {
        return get(path, 200);
    }

    public ValidatableResponse get(String path, int expectedStatusCode) {
        return spec
                   .when()
                   // Logging the URI each time seems useful as it varies across tests
                   .log().uri()
                   .get(path)
                   .then()
                   .log().ifError()
                   .log().ifValidationFails()
                   .statusCode(expectedStatusCode);
    }
}
