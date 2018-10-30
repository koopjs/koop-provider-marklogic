import io.restassured.RestAssured;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

public class IncludeFieldsTest extends AbstractFeatureServiceTest {

    @Test
    public void testIncludeFieldsInFirstDataSourcesObject() {
        String path = "/marklogic/{service}/FeatureServer/{layer}/query?resultRecordCount={resultRecordCount}&orderByFields={orderByFields}&returnGeometry={returnGeometry}";
                RestAssured
                .given()
                  .pathParam("service", "GDeltGKG")
                  .pathParam("layer", 4)
                  .pathParam("resultRecordCount", 5)
                  .pathParam("orderByFields", "name&nbspASC")
                  .pathParam("returnGeometry", true)

                .when()
                .log().uri()
                .get(path)

                .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()

                .body("features.size()", is(5))

                .body("features[0].attributes.OBJECTID", is(20643))
                .body("features[0].attributes.urlpubtimedate", is(1495616400000l))
                .body("features[0].attributes.urlpubdate", is(1495584000000l))
                .body("features[0].attributes.url", is("http://satnews.com/story.php?number=1191513746"))
                .body("features[0].attributes.name", is("Aalborg, Nordjylland, Denmark"))

                .body("features[0].attributes", not(hasKey("urltone")))
                .body("features[0].attributes", not(hasKey("domain")))
                .body("features[0].attributes", not(hasKey("urllangcode")))
                .body("features[0].attributes", not(hasKey("geores")))
        ;
    }

    @Test
    public void testIncludeFieldsInOriginalSource() {
        String path = "/marklogic/{service}/FeatureServer/{layer}/query?resultRecordCount={resultRecordCount}&orderByFields={orderByFields}&returnGeometry={returnGeometry}";
        RestAssured
                .given()
                .pathParam("service", "GDeltGKG")
                .pathParam("layer", 5)
                .pathParam("resultRecordCount", 5)
                .pathParam("orderByFields", "name&nbspASC")
                .pathParam("returnGeometry", true)

                .when()
                .log().uri()
                .get(path)

                .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()

                .body("features.size()", is(5))

                .body("features[0].attributes.OBJECTID", is(20643))
                .body("features[0].attributes.urlpubtimedate", is(1495616400000l))
                .body("features[0].attributes.urlpubdate", is(1495584000000l))
                .body("features[0].attributes.url", is("http://satnews.com/story.php?number=1191513746"))
                .body("features[0].attributes.name", is("Aalborg, Nordjylland, Denmark"))

                .body("features[0].attributes", not(hasKey("urltone")))
                .body("features[0].attributes", not(hasKey("domain")))
                .body("features[0].attributes", not(hasKey("urllangcode")))
                .body("features[0].attributes", not(hasKey("geores")))
        ;
    }
}
