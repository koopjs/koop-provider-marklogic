import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static org.hamcrest.Matchers.*;



    public class LibraryTest {

            	 @Test
         public void testFeatureService() {
             RestAssured
             
             .given().
             	 auth().basic("esri-connector-admin", "esri-connector-admin").
    	
    	     when().
                 get("http://localhost:9080/marklogic/GDeltGKG/FeatureServer").	     
    	     
    	     then()
    	     	.log().all()
    	     	.statusCode(200)
    	     	.body("layers.size()", is(3))
    	     	.body("maxRecordCount", is(5000))
    	     	.body("currentVersion", equalTo(10.51f));        
         }    
    
    @Test
    public void testGkgAllFields() {
    	
        RestAssured.given().
        	 auth().basic("esri-connector-admin", "esri-connector-admin").	     

	     when().
            get("http://localhost:9080/marklogic/GDeltGKG/FeatureServer/0/query?resultRecordCount=5&outFields=*").
        
         then()
         	.log().all()
         	.statusCode(200)
         	.body("objectIdFieldName", is("OBJECTID"))
         	.body("fields.size()", is(8));
         	
    }

    @Test
    public void testGkgCountLayer0() {
    	
        RestAssured.given().
        	 auth().basic("esri-connector-admin", "esri-connector-admin").	     

	     when().
            get("http://localhost:9080/marklogic/GDeltGKG/FeatureServer/0").
        
         then()
         	.log().all()
         	.statusCode(200)
         	.body("fields.size()", is(8))
         	;
         	
    }
    
    @Test
    public void testGkgCountLayer1() {
    	
        RestAssured.given().
        	 auth().basic("esri-connector-admin", "esri-connector-admin").	     

	     when().
            get("http://localhost:9080/marklogic/GDeltGKG/FeatureServer/1").
        
         then()
         	.log().all()
         	.statusCode(200)
         	.body("fields.size()", is(8))
         	;
         	
    }
 }