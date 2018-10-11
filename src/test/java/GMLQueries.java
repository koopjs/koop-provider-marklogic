import static org.hamcrest.Matchers.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.hamcrest.core.IsNull;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import io.restassured.RestAssured;

public class GMLQueries  extends AbstractFeatureServiceTest {

	@Test
    public void testGMLPolygon1() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[60.99609375,9.96885060854611],[86.1328125,9.96885060854611],[86.1328125,37.78808138412046],[60.99609375,37.78808138412046],[60.99609375,9.96885060854611]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(14))
                .body("features.attributes.name", hasItems("Kerala","Himachal Pradesh","Odisha","Chhattisgarh","Madhya Pradesh","Uttar Pradesh","Jammu and Kashmir","Karnataka","Rajasthan","Maharashtra","Gujarat","Haryana","Tamil Nadu","Telangana"))
            ;
		}
	
	@Test
    public void testGMLGeometryPolygon1() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&returnGeometry=true";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[60.99609375,9.96885060854611],[86.1328125,9.96885060854611],[86.1328125,37.78808138412046],[60.99609375,37.78808138412046],[60.99609375,9.96885060854611]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(14))
                .body("features.attributes.name", hasItems("Kerala","Himachal Pradesh","Odisha","Chhattisgarh","Madhya Pradesh","Uttar Pradesh","Jammu and Kashmir","Karnataka","Rajasthan","Maharashtra","Gujarat","Haryana","Tamil Nadu","Telangana"))
                .body("features.geometry.size()", is(14))
                .body("features.geometry.points.size()", not(0))
                ;
		}
	
	@Test
    public void testGMLPolygon2() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[54.58007812499999,2.3723687086440504],[105.99609375,2.3723687086440504],[105.99609375,41.178653972331674],[54.58007812499999,41.178653972331674],[54.58007812499999,2.3723687086440504]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(17))
                .body("features.attributes.name", hasItems("Kerala","Himachal Pradesh","Odisha","Chhattisgarh","Madhya Pradesh","Uttar Pradesh","Jammu and Kashmir","Karnataka","Rajasthan","Maharashtra","Gujarat","Haryana","Tamil Nadu","Telangana","West Bengal","Assam","Tripura"))
            ;
		}
	
	@Test
    public void testGMLGeometryPolygon2() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&returnGeometry=true";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[54.58007812499999,2.3723687086440504],[105.99609375,2.3723687086440504],[105.99609375,41.178653972331674],[54.58007812499999,41.178653972331674],[54.58007812499999,2.3723687086440504]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(17))
                .body("features.geometry.size()", is(17))
                .body("features.geometry.points.size()", not(0))      
                .body("features.attributes.name", hasItems("Kerala","Himachal Pradesh","Odisha","Chhattisgarh","Madhya Pradesh","Uttar Pradesh","Jammu and Kashmir","Karnataka","Rajasthan","Maharashtra","Gujarat","Haryana","Tamil Nadu","Telangana","West Bengal","Assam","Tripura"))
                ;
		}
	
	@Test
    public void testGMLPolygon3() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[70.57617187499999,25.720735134412106],[83.5400390625,25.720735134412106],[83.5400390625,34.08906131584994],[70.57617187499999,34.08906131584994],[70.57617187499999,25.720735134412106]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(5))
                .body("features.attributes.name", hasItems("Himachal Pradesh","Uttar Pradesh","Jammu and Kashmir","Rajasthan","Haryana"))
            ;
		}
	
	@Test
    public void testGMLGeometryPolygon3() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&returnGeometry=true";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[70.57617187499999,25.720735134412106],[83.5400390625,25.720735134412106],[83.5400390625,34.08906131584994],[70.57617187499999,34.08906131584994],[70.57617187499999,25.720735134412106]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(5))
                .body("features.geometry.size()", is(5))
                .body("features.geometry.points.size()", not(0))      
                .body("features.attributes.name", hasItems("Himachal Pradesh","Uttar Pradesh","Jammu and Kashmir","Rajasthan","Haryana"))
            ;
		}
	
	@Test
    public void testGMLPolygon4() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[74.81689453125,24.666986385216273],[76.6845703125,24.666986385216273],[76.6845703125,25.64152637306577],[74.81689453125,25.64152637306577],[74.81689453125,24.666986385216273]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(0))
            ;
		}
	
	@Test
    public void testGMLPolygon5() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[69.345703125,20.097206227083888],[74.1357421875,20.097206227083888],[74.1357421875,24.026396666017327],[69.345703125,24.026396666017327],[69.345703125,20.097206227083888]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features.attributes.name", hasItems("Gujarat"))
            ;
		}
	
	@Test
    public void testGMLGeometryPolygon5() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&returnGeometry=true";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[69.345703125,20.097206227083888],[74.1357421875,20.097206227083888],[74.1357421875,24.026396666017327],[69.345703125,24.026396666017327],[69.345703125,20.097206227083888]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features.geometry.size()", is(1))
                .body("features.geometry.points.size()", not(0))      
                .body("features.attributes.name", hasItems("Gujarat"))
            ;
		}

	// Envelope Test cases 
	@Test
    public void testGMLEnvelope1() {

        String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "-61,-170,85,180")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(14))
                .body("features.attributes.name", hasItems("Kerala","Himachal Pradesh","Odisha","Chhattisgarh","Madhya Pradesh","Uttar Pradesh","Jammu and Kashmir","Karnataka","Rajasthan","Maharashtra","Gujarat","Haryana","Tamil Nadu","Telangana"))            ;
	   }
	
	@Test
    public void testGMLGeometryEnvelope1() {

        String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&returnGeometry=true";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "-61,-170,85,180")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(14))
                .body("features.attributes.name", hasItems("Kerala","Himachal Pradesh","Odisha","Chhattisgarh","Madhya Pradesh","Uttar Pradesh","Jammu and Kashmir","Karnataka","Rajasthan","Maharashtra","Gujarat","Haryana","Tamil Nadu","Telangana"))
                .body("features.geometry.size()", is(14))
                .body("features.geometry.points.size()", not(0))
            ;
	   }
	
	@Test
    public void testGMLEnvelope2() {

        String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "38.84765625,-4.565473550710278,126.5625,48.22467264956519")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(17))     
                .body("features.attributes.name", hasItems("Kerala","Himachal Pradesh","Odisha","Chhattisgarh","Madhya Pradesh","Uttar Pradesh","Jammu and Kashmir","Karnataka","Rajasthan","Maharashtra","Gujarat","Haryana","Tamil Nadu","Telangana","West Bengal","Assam","Tripura"))
                ;
	   }
	
	@Test
    public void testGMLGeometryEnvelope2() {

        String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&returnGeometry=true";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "38.84765625,-4.565473550710278,126.5625,48.22467264956519")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(17))
                .body("features.geometry.size()", is(17))
                .body("features.geometry.points.size()", not(0))      
                .body("features.attributes.name", hasItems("Kerala","Himachal Pradesh","Odisha","Chhattisgarh","Madhya Pradesh","Uttar Pradesh","Jammu and Kashmir","Karnataka","Rajasthan","Maharashtra","Gujarat","Haryana","Tamil Nadu","Telangana","West Bengal","Assam","Tripura"))
                ;
	   }
	
	@Test
    public void testGMLEnvelope3() {

        String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "71.82861328125,25.423431426334222,82.24365234375,34.10725639663118")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(5))    
                .body("features.attributes.name", hasItems("Himachal Pradesh","Uttar Pradesh","Jammu and Kashmir","Rajasthan","Haryana"))
            ;
	   }
	
	@Test
    public void testGMLGeometryEnvelope3() {

        String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&returnGeometry=true";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "71.82861328125,25.423431426334222,82.24365234375,34.10725639663118")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(5))
                .body("features.geometry.size()", is(5))
                .body("features.geometry.points.size()", not(0))      
                .body("features.attributes.name", hasItems("Himachal Pradesh","Uttar Pradesh","Jammu and Kashmir","Rajasthan","Haryana"))
            ;
	   }
	

	@Test
    public void testGMLEnvelope4() {

        String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "82.9248046875,33.5963189611327,85.341796875,33.5963189611327")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(0))    
            ;
	   }
	
	@Test
    public void testGMLEnvelope5() {

        String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "69.345703125,20.097206227083888,74.1357421875,24.026396666017327")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features.attributes.name", hasItems("Gujarat"))
                ;
	   }
	
	@Test
    public void testGMLGeometryEnvelope5() {

        String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&returnGeometry=true";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "69.345703125,20.097206227083888,74.1357421875,24.026396666017327")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features.geometry.size()", is(1))
                .body("features.geometry.points.size()", not(0))      
                .body("features.attributes.name", hasItems("Gujarat"));
	   }
	
	//Point test cases
	@Test
    public void testGMLPoint1() throws UnsupportedEncodingException, ParseException  {
		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";

		String GeometryEncoded = URLEncoder.encode("{\"x\" : 73.432617, \"y\" : 27.391277}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPoint")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features[0].attributes.name", is("Rajasthan"))
            ;
		}
	
	@Test
    public void testGMLGeometryPoint1() throws UnsupportedEncodingException, ParseException  {
		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&returnGeometry=true";

		String GeometryEncoded = URLEncoder.encode("{\"x\" : 73.432617, \"y\" : 27.391277}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPoint")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features.geometry.size()", is(1))
                .body("features.geometry.points.size()", not(0)) 
                .body("features[0].attributes.name", is("Rajasthan"))
            ;
		}
	
	@Test
    public void testGMLPoint2() throws UnsupportedEncodingException, ParseException  {
		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";

		String GeometryEncoded = URLEncoder.encode("{\"x\" : 92.46093749999999, \"y\" : 39.095962936305476}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPoint")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(0))
            ;
		}
	
	@Test
    public void testGMLPoint3() throws UnsupportedEncodingException, ParseException  {
		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";

		String GeometryEncoded = URLEncoder.encode("{\"x\" : 84.803467, \"y\" : 20.940920}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPoint")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features[0].attributes.name", is("Odisha"))
            ;
		}
	
	@Test
    public void testGMLGeometryPoint3() throws UnsupportedEncodingException, ParseException  {
		String path = "/marklogic/GeoLocation/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&returnGeometry=true";

		String GeometryEncoded = URLEncoder.encode("{\"x\" : 84.803467, \"y\" : 20.940920}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            	.pathParam("geometryType", "esriGeometryPoint")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features.geometry.size()", is(1))
                .body("features.geometry.points.size()", not(0)) 
                .body("features[0].attributes.name", is("Odisha"))
            ;
		}
	
	@Test
    public void testGMLAllFields() throws UnsupportedEncodingException, ParseException  {

		String path = "marklogic/GeoLocation/FeatureServer/{layer}/query?outFields=*";
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 0)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(17))
                .body("features.geometry.size()", is(17))
                .body("features.geometry.points.size()", not(0))      
                .body("features.attributes.name", hasItems("Kerala","Himachal Pradesh","Odisha","Chhattisgarh","Madhya Pradesh","Uttar Pradesh","Jammu and Kashmir","Karnataka","Rajasthan","Maharashtra","Gujarat","Haryana","Tamil Nadu","Telangana","West Bengal","Assam","Tripura"))
                ;
		}
	}



///marklogic/GeoLocation/FeatureServer/0/query?outFields=*