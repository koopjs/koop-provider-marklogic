import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static org.hamcrest.Matchers.*;

import org.hamcrest.core.IsNull;



public class ServiceDescriptorTest extends AbstractFeatureServiceTest {

    @Test
    public void getServiceDescriptor() {
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

                .body("layers.size()", is(8))
                .body("layers.name", hasItems("GKG level 1", "GKG level 2", "GKG level 3", "GKG level 4"))

                // Koop only defines useStandardizedQueries when returning a layer descriptor
                .body("layers[0]", not(hasKey("useStandardizedQueries")))
            ;
    }

    @Test
    public void getLayerDescriptor() {
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

                // Koop appears to add this automatically, and we haven't found a way for GDS to override it - which is fine,
                // as we want this to be true since MarkLogic doesn't have a custom SQL dialect to support.
                // Per https://enterprise.arcgis.com/en/server/latest/administer/windows/about-standardized-queries.htm,
                // it seems expected that this defaults to "true" - i.e. if it's not set, then standardized queries are
                // required.
                .body("useStandardizedQueries", is(true))

                // useStandardizedQueries also appears under advancedQueryCapabilities, which is shown in some of the
                // examples at https://developers.arcgis.com/rest/services-reference/enterprise/layer-feature-service-.htm .
                .body("advancedQueryCapabilities.useStandardizedQueries", is(true))
            ;
    }

    @Test
    public void getAllLayerDescriptorsInFeatureService() {
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
                .body("layers.size()", is(8))
                .body("layers.name", hasItems("GKG level 1", "GKG level 2", "GKG level 3","GKG level 4"))

                // Just like when requesting a single layer descriptor, useStandardizedQueries is expected to be present
                // and to be true for each layer.
                .body("layers[0].useStandardizedQueries", is(true))
                .body("layers[0].advancedQueryCapabilities.useStandardizedQueries", is(true))
            ;
    }
}


