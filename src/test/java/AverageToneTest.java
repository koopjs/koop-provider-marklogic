import static org.hamcrest.Matchers.is;

import org.junit.Test;

import io.restassured.RestAssured;

public class AverageToneTest extends AbstractFeatureServiceTest {

	@Test
    public void testAverageTone() {

        String path = request2path("gkgAvgTone.json");

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
                
                .body("fields.size()", is(2))
                .body("fields[0].name", is("domain"))
                .body("fields[0].type", is("esriFieldTypeString"))
                .body("fields[0].alias", is("domain"))
                .body("fields[0].length", is(128))
                .body("fields[1].name", is("average_urltone"))
                .body("fields[1].type", is("esriFieldTypeDouble"))
                .body("fields[1].alias", is("average_urltone"))
                
                .body("features.size()", is(2123))
                .body("features[0].attributes.domain", is("newsbeast.gr"))
                .body("features[0].attributes.average_urltone", is(12.96f))
                
                .body("features[9].attributes.domain", is("stardaily.com.cn"))
                .body("features[9].attributes.average_urltone", is(7.99866666666667f))
            ;
    }
	
}
