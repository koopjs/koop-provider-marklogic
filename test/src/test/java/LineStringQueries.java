import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import io.restassured.RestAssured;

public class LineStringQueries extends AbstractFeatureServiceTest{

	//Crosses Single line Expected : Holly St
	@Test
    public void testLineStringCrosses1() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
		String GeometryEncoded = URLEncoder.encode("{\"paths\":[[[-122.26143836975098,37.51217686964284],[-122.25603103637695,37.50897690205704]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPolyline")
            	.pathParam("geometry",GeometryEncoded)
                .pathParam("spatialRel","esrispatialrelcrosses")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features[0].attributes.name", is("Holly St"))
                ;                                                       
		}
	
	//Crosses Double line = Expected : WildLife refuge
		@Test
	    public void testLineStringCrosses2() throws UnsupportedEncodingException, ParseException  {

			String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
			String GeometryEncoded = URLEncoder.encode("{\"paths\":[[[-122.2555160522461,37.51660213066696],[-122.25422859191895,37.51020243776711]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
			RestAssured.urlEncodingEnabled = false;
	 		
	    	RestAssured
	            .given()
	            	.pathParam("layer", 3)
	            	.pathParam("geometryType", "esriGeometryPolyline")
	            	.pathParam("geometry",GeometryEncoded)
	                .pathParam("spatialRel","esrispatialrelcrosses")
	            .when()
	                .log().uri()
	                .get(path)
	            .then()
	                .log().ifError()
	                .statusCode(200)
	                .body("features.size()", is(2))
	                .body("features.attributes.name", hasItems("Hwy 101","Holly St"));
	                ;                                                       
			}
		
		//Crosses Lines and Polygons = Expected : 6 features
				@Test
			    public void testLineStringCrosses3() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"paths\":[[[-122.25688934326172,37.51707868158789],[-122.25680351257324,37.511291785950505],[-122.24864959716797,37.50169135780772],[-122.24985122680663,37.514083168101116],[-122.24375724792479,37.512721531313645]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolyline")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esrispatialrelcrosses")
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(6))    
			                .body("features.attributes.name", hasItems("MarkLogic Neighborhood","Shopping Center","Wildlife Refuge","Hwy 101","Holly St","Airport"))            ;                                                       			             
			                ;                                                       
					}
				
				//Crosses Polygons = Expected : WildLife refuge
				@Test
			    public void testLineStringCrosses4() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"paths\":[[[-122.2397232055664,37.51925716132821],[-122.23809242248537,37.505436352534616]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolyline")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esrispatialrelcrosses")
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(1))    
			                .body("features[0].attributes.name", is("Wildlife Refuge"))
			                ;                                                       
					}
				
				//http://localhost:9080/marklogic/GDeltGKG/FeatureServer/3/query?geometryType=esriGeometryPolygon&geometry={"paths":[[[-122.24143981933594,37.520720791683374],[-122.24156856536865,37.51432145198483]]],"spatialReference" : {"wkid" : 4326}}

				//Intersect single Polygons = Expected : WildLife refuge
				@Test
			    public void testLineStringIntersect1() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
					String GeometryEncoded = URLEncoder.encode("{\"paths\":[[[-122.24143981933594,37.520720791683374],[-122.24156856536865,37.51432145198483]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolyline")
			            	.pathParam("geometry",GeometryEncoded)
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(1))    
			                .body("features[0].attributes.name", is("Wildlife Refuge"))
			                ;                                                       
					}
				
				//Intersect Two Polygons = Expected : WildLife refuge, MLNH
				@Test
			    public void testLineStringIntersect2() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
					String GeometryEncoded = URLEncoder.encode("{\"paths\":[[[-122.24324226379393,37.5124492009751],[-122.24710464477538,37.51210878665452]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolyline")
			            	.pathParam("geometry",GeometryEncoded)
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(2))
			                .body("features.attributes.name", hasItems("Wildlife Refuge","MarkLogic Neighborhood"));
			                ;                                                       
					}
				
				//Intersect Multiple Polygons and LineString = Expected : 4 features
				@Test
			    public void testLineStringIntersect3() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
					String GeometryEncoded = URLEncoder.encode("{\"paths\":[[[-122.25414276123047,37.505844886049545],[-122.25457191467285,37.511496036964935],[-122.24684715270996,37.51217686964284],[-122.24135398864748,37.51108753437713],[-122.24195480346678,37.50918115940604]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolyline")
			            	.pathParam("geometry",GeometryEncoded)
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(4))
			                .body("features.attributes.name", hasItems("Airport","Wildlife Refuge","MarkLogic Neighborhood","Hwy 101"));
			                ;                                                       
					}
				
				//Complete inside polygon without Intersection = Expected :MLNH
				@Test
			    public void testLineStringIntersect4() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
					String GeometryEncoded = URLEncoder.encode("{\"paths\":[[[-122.25654602050781,37.51081519807655],[-122.25688934326172,37.50693429782622]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolyline")
			            	.pathParam("geometry",GeometryEncoded)
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(1))
			                .body("features[0].attributes.name", is("MarkLogic Neighborhood"))			                ;                                                       
					}
				
				//Reverse test -polyline with one end as a point in database  Expected : MLNH

				@Test
			    public void testLineStringIntersect5() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
					String GeometryEncoded = URLEncoder.encode("{\"paths\":[[[-122.26075172424316,37.511836454080196],[-122.2582,37.5128]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolyline")
			            	.pathParam("geometry",GeometryEncoded)
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(1))
			                .body("features[0].attributes.name", is("MarkLogic Neighborhood"))			                ;                                                       
					}
}
