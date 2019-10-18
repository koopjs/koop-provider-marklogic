import static org.hamcrest.Matchers.is;

import org.junit.Test;

import io.restassured.RestAssured;

public class PubtimeErrorTest extends AbstractFeatureServiceTest{

	@Test
    public void testGkgPubtimeError0() {

        String path = request2path("gkgPubtimeError.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
	            .log().ifError()
	            .statusCode(200)
	            .log().ifValidationFails()
	            .body("count", is(0))
                ;
    }
}
