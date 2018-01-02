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
            .body("fields[0].alias", is("OBJECTID"))
            .body("fields[0].length", IsNull.nullValue())
            .body("fields[0].editable", is(false))
            .body("fields[0].nullable", is(true))
            .body("fields[0].domain", IsNull.nullValue())
            .body("fields[2].name", is("domain"))
            .body("fields[2].type", is("esriFieldTypeString"))
            .body("fields[2].alias", is("domain"))
            .body("fields[2].length", IsNull.nullValue())
            .body("fields[2].editable", is(false))
            .body("fields[2].nullable", is(true))
            .body("fields[2].domain", IsNull.nullValue())

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

            .body("exceededTransferLimit", is(false))
        ;
    }

}
