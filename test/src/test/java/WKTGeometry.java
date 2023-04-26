import static org.hamcrest.Matchers.*;

import java.io.UnsupportedEncodingException;

import org.json.simple.parser.ParseException;
import org.junit.Test;

public class WKTGeometry  extends AbstractFeatureServiceTest {

    // "geometry" : {
    //   "type" : "Polygon",
    //   "format" : "wkt",
    //   "coordinateSystem" : "wgs84",
    //   "source" : {
    //     "xpath" : "/envelope/instance/boundary"
    //   }
    // }

    @Test
    public void testXPathExtraction() throws UnsupportedEncodingException, ParseException  {

        String path = basePath("GeoLocation") + "/FeatureServer/{layer}/query?ids={ids}&returnGeometry={returnGeometry}";

        RestAssuredHelper helper = new RestAssuredHelper();
        helper.pathParam("layer", 6)
              .pathParam("ids", 900001)
              .pathParam("returnGeometry", true);

        helper.get(path)
                .body("features.size()", is(1))
                .body("features.geometry.rings.size()", is(1))
                .body("features.geometry.rings[0][0].size()", is(5))
                .body("features.geometry.rings[0][0][0].size()", is(2))
                .body("features.geometry.rings[0][0][0]", hasItems(30, 10))
            ;
    }

    // "geometry" : {
    //   "type" : "Polygon",
    //   "format" : "wkt",
    //   "coordinateSystem" : "wgs84",
    //   "source" : {
    //     "column" : "boundary"
    //   }
    // }
    @Test
    public void testColumnExtraction() throws UnsupportedEncodingException, ParseException  {

        String path = basePath("GeoLocation") + "/FeatureServer/{layer}/query?ids={ids}&returnGeometry={returnGeometry}";

        RestAssuredHelper helper = new RestAssuredHelper();
        helper.pathParam("layer", 7)
              .pathParam("ids", 900001)
              .pathParam("returnGeometry", true);

        helper.get(path)
                .body("features.size()", is(1))
                .body("features.geometry.rings.size()", is(1))
                .body("features.geometry.rings[0][0].size()", is(5))
                .body("features.geometry.rings[0][0][0].size()", is(2))
                .body("features.geometry.rings[0][0][0]", hasItems(30, 10))
            ;
    }

}
