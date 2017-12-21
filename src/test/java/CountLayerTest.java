import static org.hamcrest.Matchers.is;

import org.junit.Test;

import io.restassured.RestAssured;

public class CountLayerTest extends AbstractFeatureServiceTest{

	@Test
    public void testGkgCountLayer0() {

        String path = request2path("gkgCountLayer0.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(38765))
            ;
    }
	
	@Test
    public void testGkgCountLayer1() {

        String path = request2path("gkgCountLayer1.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(3557))
            ;
    }
	
	
}
