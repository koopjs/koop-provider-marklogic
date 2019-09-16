import static org.hamcrest.Matchers.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.hamcrest.core.IsNull;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import io.restassured.RestAssured;

public class GeoJSONGeometry  extends AbstractFeatureServiceTest {

    // "geometry" : {
    //   "type" : "Polygon",
    //   "format" : "geojson",
    //   "coordinateSystem" : "wgs84",
    //   "source" : {
    //     "xpath" : "/envelope/instance/boundary"
    //   }
    // }

    @Test
    public void testXPathExtraction() throws UnsupportedEncodingException, ParseException  {

        String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?ids={ids}&returnGeometry={returnGeometry}";

        RestAssured
            .given()
              .pathParam("layer", 8)
              .pathParam("ids", 990001)
              .pathParam("returnGeometry", true)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features.geometry.rings.size()", is(1))
                .body("features.geometry.rings[0][0].size()", is(5))
                .body("features.geometry.rings[0][0][0].size()", is(2))
                .body("features.geometry.rings[0][0][0]", hasItems(30, 10))
            ;
    }

    // "geometry" : {
    //   "type" : "Polygon",
    //   "format" : "geojson",
    //   "coordinateSystem" : "wgs84",
    //   "source" : {
    //     "column" : "boundary"
    //   }
    // }
    @Test
    public void testColumnExtraction() throws UnsupportedEncodingException, ParseException  {

        String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?ids={ids}&returnGeometry={returnGeometry}";

        RestAssured
            .given()
              .pathParam("layer", 9)
              .pathParam("ids", 990001)
              .pathParam("returnGeometry", true)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features.geometry.rings.size()", is(1))
                .body("features.geometry.rings[0][0].size()", is(5))
                .body("features.geometry.rings[0][0][0].size()", is(2))
                .body("features.geometry.rings[0][0][0]", hasItems(30, 10))
            ;
    }

    // "geometry" : {
    //   "type" : "Polygon",
    //   "format" : "geojson",
    //   "coordinateSystem" : "wgs84",
    //   "source" : {
    //     "xpath" : "/envelope/header/ctsRegion"
    //     "format" : "cts",
    //   }
    // }
    @Test
    public void testXPathCtsExtraction() throws UnsupportedEncodingException, ParseException  {

        String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?ids={ids}&returnGeometry={returnGeometry}";

        RestAssured
            .given()
              .pathParam("layer", 10)
              .pathParam("ids", 990001)
              .pathParam("returnGeometry", true)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features.geometry.rings.size()", is(1))
                .body("features.geometry.rings[0][0].size()", is(5))
                .body("features.geometry.rings[0][0][0].size()", is(2))
                .body("features.geometry.rings[0][0][0]", hasItems(30, 10))
            ;
    }

}
