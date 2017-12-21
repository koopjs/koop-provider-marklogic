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
            .body("objectIdFieldName", is("OBJECTID"))
            .body("globalIdFieldName", is(""))
            .body("hasZ", is(false))
            .body("hasM", is(false))
            
            .body("spatialReference.wkid", is(4326))
            
            .body("fields.size()", is(9))
            .body("fields[0].name", is("OBJECTID"))
            .body("fields[0].type", is("esriFieldTypeOID"))
            .body("fields[0].alias", is("OBJECTID"))
            .body("fields[0].length", IsNull.nullValue())
            .body("fields[0].editable", is(false))
            .body("fields[0].nullable", is(true))
            .body("fields[0].domain", IsNull.nullValue())
            .body("fields[8].name", is("geores"))
            .body("fields[8].type", is("esriFieldTypeInteger"))
            .body("fields[8].alias", is("geores"))
            .body("fields[8].length", IsNull.nullValue())
            .body("fields[8].editable", is(false))
            .body("fields[8].nullable", is(true))
            .body("fields[8].domain", IsNull.nullValue())
            
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
            
            .body("features[9].attributes.OBJECTID", is(9603))
            .body("features[9].attributes.urlpubtimedate", is(1495605600000L))
            .body("features[9].attributes.urlpubdate", is(1495584000000L))
            .body("features[9].attributes.url", is("http://zpravy.idnes.cz/platy-poslanci-senatori-prezident-2016-dwn-/domaci.aspx"))
            .body("features[9].attributes.name", is("Czech Republic"))
            .body("features[9].attributes.urltone", is(-1.78f))
            .body("features[9].attributes.domain", is("zpravy.idnes.cz"))
            .body("features[9].attributes.urllangcode", is("ces"))
            .body("features[9].attributes.geores", is(1))
            
            .body("exceededTransferLimit", is(false))
        ;
    }

}
