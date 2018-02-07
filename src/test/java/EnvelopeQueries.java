
import org.junit.Test;

import io.restassured.RestAssured;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.*;

public class EnvelopeQueries extends AbstractFeatureServiceTest{

	//  Default operation is : Intersects
	@Test
    public void testEnvelopeMarkLogicNeighborhoodIntersects() {

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
                //.body("features..name", hasItems("Museum", "MarkLogic Neighborhood", "Wildlife Refuge","Airport","Hwy 101","Holly St"))
                .body("features[0].attributes.name", is("MarkLogic HQ"))
                .body("features[1].attributes.name", is("Museum"))
                .body("features[2].attributes.name", is("Restaurant"))
                .body("features[3].attributes.name", is("MarkLogic Neighborhood"))
                .body("features[4].attributes.name", is("Wildlife Refuge"))
                .body("features[5].attributes.name", is("Shopping Center"))
                .body("features[6].attributes.name", is("Airport"))
                .body("features[7].attributes.name", is("Hwy 101"))
                .body("features[8].attributes.name", is("Holly St"))
            ;
	}
	
	@Test
    public void testEnvelopeAirportIntersects() {

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
                //.body("features..name", hasItems("Museum", "MarkLogic Neighborhood", "Wildlife Refuge","Airport","Hwy 101","Holly St"))
                .body("features[0].attributes.name", is("Museum"))
                .body("features[1].attributes.name", is("MarkLogic Neighborhood"))
                .body("features[2].attributes.name", is("Wildlife Refuge"))
                .body("features[3].attributes.name", is("Airport"))
                .body("features[4].attributes.name", is("Hwy 101"))
                .body("features[5].attributes.name", is("Holly St"))
            ; 
	}	
	
	@Test
    public void testEnvelopeShoppingCentreIntersects() {

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
	
	@Test
    public void testEnvelopeWildlifeRefugeIntersects() {

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
                .body("features[0].attributes.name", is("MarkLogic HQ"))
                .body("features[1].attributes.name", is("MarkLogic Neighborhood"))
                .body("features[2].attributes.name", is("Wildlife Refuge"))
                .body("features[3].attributes.name", is("Airport"))
            ; 
	}

	@Test
    public void testEnvelopeAroundPointIntersects() {

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
                .body("features[0].attributes.name", is("Restaurant"))
                .body("features[1].attributes.name", is("MarkLogic Neighborhood"))
            ; 
	}
	
	@Test
    public void testEnvelopeEmptyIntersects() {

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
	
}
