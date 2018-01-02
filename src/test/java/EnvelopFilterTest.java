import static org.hamcrest.Matchers.is;

import org.junit.Test;

import io.restassured.RestAssured;

public class EnvelopFilterTest extends AbstractFeatureServiceTest {

	@Test
    public void testGkgEnvelopFilter() {

        String path = request2pathWithEncoding("gkgEnvelopeFilter.json");
    	RestAssured.urlEncodingEnabled = false;

        try{
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
        finally{
        	RestAssured.reset();
        }
	}
}
