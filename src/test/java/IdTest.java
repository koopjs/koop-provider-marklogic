import static org.hamcrest.Matchers.is;

import java.awt.List;
import org.junit.Test;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;

import io.restassured.RestAssured;

public class IdTest extends AbstractFeatureServiceTest {

	@Test
    public void testGkgIdsOnly() {

        String path = request2path("gkgIdsOnly.json");


        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("objectIdField", is("OBJECTID"))
                ;
    }

	@Test
    public void testGkgObjectIds() {

        String path = request2path("gkgObjectIds.json");


        RestAssured
        .given()
        .when()
            .log().uri()
            .get(path)

        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()

            .body("features.size()", is(2))
            .body("features[1].attributes.OBJECTID", is(56576))
            .body("features[1].attributes.urlpubtimedate", is(1495636200000L))
            .body("features[1].attributes.urlpubdate", is(1495584000000L))
            .body("features[1].attributes.url", is("http://www.bendigoadvertiser.com.au/story/4685559/meet-the-real-high-taxpayers-theyre-not-high-earners/"))
            .body("features[1].attributes.name", is("Australia"))
            .body("features[1].attributes.urltone", is(-3.91f))
            .body("features[1].attributes.domain", is("bendigoadvertiser.com.au"))
            .body("features[1].attributes.urllangcode", is("eng"))
            .body("features[1].attributes.geores", is(1))

            .body("features[0].attributes.OBJECTID", is(56577))
            .body("features[0].attributes.urlpubtimedate", is(1495636200000L))
            .body("features[0].attributes.urlpubdate", is(1495584000000L))
            .body("features[0].attributes.url", is("http://www.bendigoadvertiser.com.au/story/4685559/meet-the-real-high-taxpayers-theyre-not-high-earners/"))
            .body("features[0].attributes.name", is("Australia"))
            .body("features[0].attributes.urltone", is(-3.91f))
            .body("features[0].attributes.domain", is("bendigoadvertiser.com.au"))
            .body("features[0].attributes.urllangcode", is("eng"))
            .body("features[0].attributes.geores", is(1))
        ;
    }


}

//"24457","24973","5632","24974","27161","56371","49518","49416","32295","32309","32296","47923","32293","8384","44483","32724","22445","22455","1807","5538"
