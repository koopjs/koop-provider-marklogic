
import org.junit.Test;

import io.restassured.RestAssured;

import static org.hamcrest.Matchers.*;

public class EnvelopeQueries extends AbstractFeatureServiceTest{

	//====================================Intersect========================================
	//  Default operation is : Intersects
	//testEnvelopeMarkLogicNeighborhoodIntersects
	@Test
    public void testEnvelopeIntersects1() {

        String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "-122.2634554,37.5033596,-122.2446156,37.5212994")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(9))
                .body("features.attributes.name", hasItems("MarkLogic HQ","Museum","Restaurant","Shopping Center","MarkLogic Neighborhood", "Wildlife Refuge","Airport","Hwy 101","Holly St"))
            ;
	}
	
	//testEnvelopeAirportIntersects
	@Test
    public void testEnvelopeIntersects2() {

        String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "-122.2543144,37.5083982,-122.2457314,37.5181339")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(6))
                .body("features.attributes.name", hasItems("Museum","MarkLogic Neighborhood", "Wildlife Refuge","Airport","Hwy 101","Holly St"))
            ; 
	}	
	
	//testEnvelopeShoppingCentreIntersects
	@Test
    public void testEnvelopeIntersects3() {

        String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "-122.2502375,37.5003635,-122.2465038,37.5033937")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features[0].attributes.name", is("Shopping Center"))
            ; 
	}
	
	//testEnvelopeWildlifeRefugeIntersects
	@Test
    public void testEnvelopeIntersects4() {

        String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "-122.2468472,37.5069683,-122.2356892,37.517351")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(4))
                .body("features.attributes.name", hasItems("MarkLogic HQ","MarkLogic Neighborhood", "Wildlife Refuge","Airport"))
            ; 
	}

	//testEnvelopeAroundPointIntersects
	@Test
    public void testEnvelopeIntersects5() {

        String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "-122.2581983,37.5128407,-122.2581983,37.5128407")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(2))
                .body("features.attributes.name", hasItems("Restaurant","MarkLogic Neighborhood"))

            ; 
	}
	
	//testEnvelopeEmptyIntersects
	@Test
    public void testEnvelopeIntersects6() {

        String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "-122.1237558,37.5503635,-122.1337558,37.565")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(0))
            ; 
	}
	
	//=========================Contains============================================
	//Inside single Envelope - Expected : MarkLogic Neighborhood
	@Test
    public void testEnvelopeContains1() {

        String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
    	RestAssured.urlEncodingEnabled = false;
 	
        RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryEnvelope")
            	.pathParam("geometry", "-122.25723266601562,37.507070473180455,-122.25337028503418,37.50904498790216")
                .pathParam("spatialRel","esrispatialrelcontains")

            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features[0].attributes.name", is("MarkLogic Neighborhood"))            ; 
	}
	
	//Inside two Envelope - Expected : MarkLogic Neighborhood , Airport
		@Test
	    public void testEnvelopeContains2() {

	        String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
	    	RestAssured.urlEncodingEnabled = false;
	 	
	        RestAssured
	            .given()
	            	.pathParam("layer", 3)
	            	.pathParam("geometryType", "esriGeometryEnvelope")
	            	.pathParam("geometry", "-122.25113868713377,37.514763977179,-122.24907875061034,37.51694252449245")
	                .pathParam("spatialRel","esrispatialrelcontains")

	            .when()
	                .log().uri()
	                .get(path)
	            .then()
	                .log().ifError()
	                .statusCode(200)
	                .body("features.size()", is(2))
	                .body("features.attributes.name", hasItems("MarkLogic Neighborhood","Airport"));
	             		}
		
		//Inside a Envelope and intersecting 2 other features - Expected : MarkLogic Neighborhood 
				@Test
			    public void testEnvelopeContains3() {

			        String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
			    	RestAssured.urlEncodingEnabled = false;
			 	
			        RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryEnvelope")
			            	.pathParam("geometry", "-122.24907875061034,37.51067902955361,-122.24547386169434,37.51367467")
			                .pathParam("spatialRel","esrispatialrelcontains")

			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(1))
			                .body("features[0].attributes.name", is("MarkLogic Neighborhood"))            ; 
			             		}
		
}
