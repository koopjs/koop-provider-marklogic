import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static org.hamcrest.Matchers.*;



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
                .body("maxRecordCount", is(5000))
                .body("layers.size()", is(3))
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
                .body("fields.size()", is(8))
                .body("fields.name", hasItems("OBJECTID", "urlpubtimedate", "url", "name", "urltone", "domain", "urllangcode", "geores"))
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
                .body("layers.size()", is(3))
                .body("layers.name", hasItems("GKG level 1", "GKG level 2", "GKG level 3"))
            ;

      // we should probably add more validation here or just add new tests if there are
      // other specific fields we want to inspect
    }
}


