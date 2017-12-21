import static org.hamcrest.Matchers.is;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsNull;
import org.junit.Test;

import io.restassured.RestAssured;

public class WhereTest extends AbstractFeatureServiceTest{

	@Test
    public void testGkgCountWhere() {

        String path = request2path("gkgCountWhere.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(197))
            ;
    }
	
	@Test
    public void testGkgWhereISNOTNULL() {

        String path = request2path("whereISNOTNULL.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(33338))
            ;
    }
	
	@Test
    public void testGkgWhereISNULL() {

        String path = request2path("whereISNULL.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(0))
            ;
    }
	
	@Test
    public void testGkgWhereIn() {

        String path = request2path("gkgWhereIn.json");

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
                
                .body("fields.size()", is(8))
                .body("fields[0].name", is("OBJECTID"))
                .body("fields[0].type", is("esriFieldTypeOID"))
                .body("fields[0].alias", is("OBJECTID"))
                .body("fields[0].length", IsNull.nullValue())
                .body("fields[0].editable", is(false))
                .body("fields[0].nullable", is(true))
                .body("fields[0].domain", IsNull.nullValue())
                .body("fields[7].name", is("geores"))
                .body("fields[7].type", is("esriFieldTypeInteger"))
                .body("fields[7].alias", is("geores"))
                .body("fields[7].length", IsNull.nullValue())
                .body("fields[7].editable", is(false))
                .body("fields[7].nullable", is(true))
                .body("fields[7].domain", IsNull.nullValue())
                
                .body("features.size()", is(2))
                .body("features[0].attributes.OBJECTID", is(56576))
                .body("features[0].attributes.urlpubtimedate", is(1495636200000L))
                .body("features[0].attributes.url", is("http://www.bendigoadvertiser.com.au/story/4685559/meet-the-real-high-taxpayers-theyre-not-high-earners/"))
                .body("features[0].attributes.name", is("Australia"))
                .body("features[0].attributes.urltone", is(-3.91f))
                .body("features[0].attributes.domain", is("bendigoadvertiser.com.au"))
                .body("features[0].attributes.urllangcode", is("eng"))
                .body("features[0].attributes.geores", is(1))
                
                .body("features[1].attributes.OBJECTID", is(56577))
                .body("features[1].attributes.urlpubtimedate", is(1495636200000L))
                .body("features[1].attributes.url", is("http://www.bendigoadvertiser.com.au/story/4685559/meet-the-real-high-taxpayers-theyre-not-high-earners/"))
                .body("features[1].attributes.name", is("Australia"))
                .body("features[1].attributes.urltone", is(-3.91f))
                .body("features[1].attributes.domain", is("bendigoadvertiser.com.au"))
                .body("features[1].attributes.urllangcode", is("eng"))
                .body("features[1].attributes.geores", is(1))
                
                .body("exceededTransferLimit", is(false))
            ;
    }
	
	
	@Test
    public void testGkgWhereNotIn() {

        String path = request2path("gkgWhereNotIn.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(33336))
            ;
    }
	
}
