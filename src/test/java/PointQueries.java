import static org.hamcrest.Matchers.is;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import io.restassured.RestAssured;

public class PointQueries  extends AbstractFeatureServiceTest {

	@Test
    public void testOnePolygonIntersects() throws UnsupportedEncodingException, ParseException  {

		//marklogic/GDeltGKG/FeatureServer/3/query?geometryType=esriGeometryPoint&geometry={"x" : -122.25972175598143, "y" : 37.51871254735555}

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"x\" : -122.25972175598143, \"y\" : 37.51871254735555}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPoint")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features[0].attributes.name", is("MarkLogic Neighborhood"))
                
            ;
		}
	
	@Test
    public void testTwoPolygonIntersects() throws UnsupportedEncodingException, ParseException  {

		//marklogic/GDeltGKG/FeatureServer/3/query?geometryType=esriGeometryPoint&geometry={"x" : -122.25972175598143, "y" : 37.51871254735555}

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"x\" : -122.24564552307129, \"y\" : 37.513198107015064}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPoint")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(2))
                .body("features[0].attributes.name", is("MarkLogic Neighborhood"))
                .body("features[1].attributes.name", is("Wildlife Refuge"))
            ;
		}
}
