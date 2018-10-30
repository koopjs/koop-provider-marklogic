import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static org.hamcrest.Matchers.*;

import org.hamcrest.core.IsNull;



public class ServiceDescriptorTest extends AbstractFeatureServiceTest {

    @Test
    public void testServiceDescriptor() {
        String path = request2path("featureService.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("currentVersion", is(10.51f))
                .body("serviceDescription", notNullValue())
                .body("hasVersionedData", is(false))
                .body("currentVersion", is(10.51f))
                .body("maxRecordCount", is(5000))
                .body("hasStaticData", is(false))
                .body("capabilities", is("Query"))
                .body("description", notNullValue())

                .body("spatialReference.wkid", is(4326))
                .body("spatialReference.latestWkid", is(4326))

                .body("initialExtent.xmin", is(-180))
                .body("initialExtent.ymin", is(-90))
                .body("initialExtent.xmax", is(180))
                .body("initialExtent.ymax", is(90))
                .body("initialExtent.spatialReference.wkid", is(4326))
                .body("initialExtent.spatialReference.latestWkid", is(4326))

                .body("fullExtent.xmin", is(-180))
                .body("fullExtent.ymin", is(-90))
                .body("fullExtent.xmax", is(180))
                .body("fullExtent.ymax", is(90))
                .body("fullExtent.spatialReference.wkid", is(4326))
                .body("fullExtent.spatialReference.latestWkid", is(4326))


                .body("allowGeometryUpdates", is(false))
                .body("units", is("esriDecimalDegrees"))
                .body("syncEnabled", is(false))

                .body("layers.size()", is(6))
                .body("layers.name", hasItems("GKG level 1", "GKG level 2", "GKG level 3"))
            ;

        // we should probably add more validation here or just add new tests if there are
        // other specific fields we want to inspect
    }

    @Test
    public void testLayerDescriptor() {
        String path = request2path("layer.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("id", is(0))
                .body("name", is("GKG level 1"))
                .body("type", is("Feature Layer"))
                .body("description", notNullValue())
                .body("geometryType", is("esriGeometryPoint"))
                .body("copyrightText", is(" "))
                .body("parentLayer", IsNull.nullValue())
                .body("subLayers", IsNull.nullValue())
                .body("minScale", is(0))
                .body("maxScale", is(0))
                .body("defaultVisibility", is(true))
                .body("extent.xmin", is(-180))
                .body("extent.ymin", is(-90))
                .body("extent.xmax", is(180))
                .body("extent.ymax", is(90))
                .body("extent.spatialReference.wkid", is(4326))
                .body("extent.spatialReference.latestWkid", is(4326))

                .body("fields.size()", is(9))
                .body("fields.name", hasItems("OBJECTID", "urlpubtimedate", "urlpubdate", "url", "name", "urltone", "domain", "urllangcode", "geores"))
                .body("hasStaticData", is(false))
            ;

      // we should probably add more validation here or just add new tests if there are
      // other specific fields we want to inspect
    }

    @Test
    public void testLayers() {
        String path = request2path("featureService.json") + "/layers";

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("layers.size()", is(6))
                .body("layers.name", hasItems("GKG level 1", "GKG level 2", "GKG level 3","GKG level 4"))
            ;

      // we should probably add more validation here or just add new tests if there are
      // other specific fields we want to inspect
    }
}


