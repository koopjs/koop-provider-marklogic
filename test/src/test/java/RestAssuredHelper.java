import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

/**
 * This is a "helper" currently for removing a bunch of duplication across hundreds of tests - the duplicated code is
 * now in this class's "get" method. The scope of this class can be expanded as needed.
 */
public class RestAssuredHelper {

    private RequestSpecification spec;

    public RestAssuredHelper() {
        spec = RestAssured.given();
    }

    public RequestSpecification pathParam(String name, Object value) {
        return spec.pathParam(name, value);
    }

    public ValidatableResponse get(String path) {
        return spec
                   .when()
                   // Logging the URI each time seems useful as it varies across tests
                   .log().uri()
                   .get(path)
                   .then()
                   .log().ifError()
                   .log().ifValidationFails()
                   .statusCode(200);
    }
}
