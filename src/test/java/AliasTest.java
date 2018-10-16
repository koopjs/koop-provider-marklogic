import io.restassured.RestAssured;
import org.hamcrest.core.IsNull;
import org.junit.Test;

import static org.hamcrest.Matchers.is;


public class AliasTest extends AbstractFeatureServiceTest {

    @Test
    public void testFieldAlias() {
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

                .body( "fields.find {it.name == 'OBJECTID'} .alias", is("OBJECTID"))
                .body( "fields.find {it.name == 'urlpubtimedate'} .alias", is("pubtime"))
                .body( "fields.find {it.name == 'urlpubdate'} .alias", is("pubdate"))
                .body( "fields.find {it.name == 'url'} .alias", is("doc_url"))
                .body( "fields.find {it.name == 'name'} .alias", is("Location"))
        ;
    }
}
