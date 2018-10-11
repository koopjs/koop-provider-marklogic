import static org.hamcrest.Matchers.is;

import org.hamcrest.core.IsNull;
import org.junit.Test;

import io.restassured.RestAssured;

public class OrderByTest extends AbstractFeatureServiceTest {

	@Test
    public void testGkgOrderbyTop10() {

        String path = request2path("gkgOrderbyTop10.json");

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

            .body("features[0].attributes.OBJECTID", is(8991))
            .body("features[0].attributes.urlpubtimedate", is(1495605600000L))
            .body("features[0].attributes.urlpubdate", is(1495584000000L))
            .body("features[0].attributes.url", is("http://zz.diena.lv/kriminalzinas/vugd/maras-ielas-kapnutelpa-deg-atkritumi-229596"))
            .body("features[0].attributes.name", is("Latvia"))
            .body("features[0].attributes.urltone", is(-2.86f))
            .body("features[0].attributes.domain", is("zz.diena.lv"))
            .body("features[0].attributes.urllangcode", is("lav"))
            .body("features[0].attributes.geores", is(1))

            .body("features[9].attributes.OBJECTID", is(31999))
            .body("features[9].attributes.urlpubtimedate", is(1495623600000L))
            .body("features[9].attributes.urlpubdate", is(1495584000000L))
            .body("features[9].attributes.url", is("http://zpravy.idnes.cz/cholera-cesko-nakaza-0la-/domaci.aspx"))
            .body("features[9].attributes.name", is("Ukraine"))
            .body("features[9].attributes.urltone", is(-0.57f))
            .body("features[9].attributes.domain", is("zpravy.idnes.cz"))
            .body("features[9].attributes.urllangcode", is("ces"))
            .body("features[9].attributes.geores", is(1))
        ;
    }

    @Test
    public void testGkgOrderbyLeadingWhitespace() {

        String path = request2path("gkgOrderbyLeadingWhitespace.json");

        RestAssured
        .given()
        .when()
            .log().uri()
            .get(path)
        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()
            .body("objectIdFieldName", is("OBJECTID"))
            .body("globalIdFieldName", is(""))
            .body("hasZ", is(false))
            .body("hasM", is(false))
            .body("spatialReference.wkid", is(4326))
            .body("features.size()", is(10))
        ;
    }

}
