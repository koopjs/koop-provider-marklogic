import static org.hamcrest.Matchers.is;

import org.hamcrest.core.IsNull;
import org.junit.Test;

import io.restassured.RestAssured;

public class OffsetAndLimitTest extends AbstractFeatureServiceTest {

	@Test
    public void testGkgOffsetAndLimit() {

        String path = request2path("gkgOffsetAndLimit.json");

        RestAssured
        .given()
        .when()
            .log().uri()
            .get(path)

        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()

            .body("features.size()", is(10))

            .body("features[0].attributes.OBJECTID", is(3728))
            .body("features[0].attributes.urlpubtimedate", is(1495605600000L))
            .body("features[0].attributes.urlpubdate", is(1495584000000L))
            .body("features[0].attributes.url", is("http://www.ziuanews.ro/stiri/cozmin-gu-dovedirea-fraud-rii-alegerilor-din-2009-este-important-pentru-democra-ia-noastr-704622"))
            .body("features[0].attributes.name", is("Romania"))
            .body("features[0].attributes.urltone", is(-1.43f))
            .body("features[0].attributes.domain", is("ziuanews.ro"))
            .body("features[0].attributes.urllangcode", is("ron"))
            .body("features[0].attributes.geores", is(1))

            .body("features[9].attributes.OBJECTID", is(25653))
            .body("features[9].attributes.urlpubtimedate", is(1495616400000L))
            .body("features[9].attributes.urlpubdate", is(1495584000000L))
            .body("features[9].attributes.url", is("http://www.zimbabwesituation.com/news/zimsit-m-govt-urged-to-export-transformers-to-raise-forex/"))
            .body("features[9].attributes.name", is("Zimbabwe"))
            .body("features[9].attributes.urltone", is(-2.99f))
            .body("features[9].attributes.domain", is("zimbabwesituation.com"))
            .body("features[9].attributes.urllangcode", is("eng"))
            .body("features[9].attributes.geores", is(1))
        ;
    }

}
