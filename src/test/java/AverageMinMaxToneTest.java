import static org.hamcrest.Matchers.is;

import org.junit.Test;

import io.restassured.RestAssured;

public class AverageMinMaxToneTest extends AbstractFeatureServiceTest {

	@Test
    public void testAverageMinMaxTone() {

        String path = request2path("gkgAvgMinMaxTone.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("displayFieldName", is(""))
                .body("fieldAliases.domain", is("domain"))
                .body("fieldAliases.average_urltone", is("average_urltone"))
                .body("fieldAliases.minimum_urltone", is("minimum_urltone"))
                .body("fieldAliases.maximum_urltone", is("maximum_urltone"))
                
                .body("fields.size()", is(4))
                .body("fields[0].name", is("domain"))
                .body("fields[0].type", is("esriFieldTypeString"))
                .body("fields[0].alias", is("domain"))
                .body("fields[0].length", is(128))
                
                .body("features.size()", is(2123))
                .body("features[0].attributes.domain", is("newsbeast.gr"))
                .body("features[0].attributes.average_urltone", is(12.96f))
                .body("features[0].attributes.minimum_urltone", is(12.96f))
                .body("features[0].attributes.maximum_urltone", is(12.96f))
            ;
    }
}
