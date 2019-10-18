import static org.hamcrest.Matchers.is;

import org.hamcrest.core.IsNull;
import org.junit.Test;

import io.restassured.RestAssured;

public class LimitFieldTest extends AbstractFeatureServiceTest{

	@Test
    public void testGkgLimitFields() {

        String path = request2path("gkgLimitFields.json");

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

            .body("fields.size()", is(3))
            .body("fields[0].name", is("OBJECTID"))
            .body("fields[0].type", is("esriFieldTypeOID"))
            .body("fields[2].name", is("domain"))
            .body("fields[2].type", is("esriFieldTypeString"))
            .body("fields[2].alias", is("domain"))

            .body("features.size()", is(20))

            .body("features[0].attributes.OBJECTID", is(1))
            .body("features[0].attributes.name", is("United Kingdom"))
            .body("features[0].attributes.domain", is("rtbf.be"))
            .body("features[0].geometry.x", is(-2))
            .body("features[0].geometry.y", is(54))

            .body("features[19].attributes.OBJECTID", is(43))
            .body("features[19].attributes.name", is("Greece"))
            .body("features[19].attributes.domain", is("candianews.gr"))
            .body("features[19].geometry.x", is(22))
            .body("features[19].geometry.y", is(39))
        ;
    }

    @Test
    public void testGkgAllLimitFields() {

        String path = request2path("gkgLimitAllFields.json");

        RestAssured
        .given()
        .when()
            .log().uri()
            .get(path)

        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()
            .body("features.size()", is(5000))
        ;
    }

    @Test
    public void testGkgLimitResultRecordCount1() {

        String path = request2path("gkgLimitResultRecordCount1.json");

        RestAssured
        .given()
        .when()
            .log().uri()
            .get(path)

        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()
            .body("objectIds.size()", is(3500))
        ;
    }

    @Test
    public void testGkgLimitResultRecordCount2() {

        String path = request2path("gkgLimitResultRecordCount2.json");

        RestAssured
        .given()
        .when()
            .log().uri()
            .get(path)

        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()
            .body("objectIds.size()", is(7000))
        ;
    }

    @Test
    public void testGkgLimitResultRecordCount3() {

        String path = request2path("gkgLimitResultRecordCount3.json");

        RestAssured
        .given()
        .when()
            .log().uri()
            .get(path)

        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()
            .body("features.size()", is(4000))
        ;
    }

    @Test
    public void testGkgLimitReturnIdsOnly0() {

        String path = request2path("gkgLimitReturnIdsOnly0.json");

        RestAssured
        .given()
        .when()
            .log().uri()
            .get(path)

        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()
            .body("objectIds.size()", is(10))
        ;
    }
}

