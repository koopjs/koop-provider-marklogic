import static org.hamcrest.Matchers.is;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Test;

import io.restassured.RestAssured;

public class AverageToneTest extends AbstractFeatureServiceTest {

	@Test
    public void testAverageTone() throws UnsupportedEncodingException {

        String path = request2pathWithEncoding("gkgAvgTone.json");        
    	RestAssured.urlEncodingEnabled = false;

    	try {
        RestAssured
            .given()

            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("displayFieldName", is(""))
                .body("fieldAliases.domain", is("domain"))
                .body("fieldAliases.average_urltone", is("average_urltone"))
                
                .body("fields.size()", is(2))
                .body("fields[0].name", is("domain"))
                .body("fields[0].type", is("esriFieldTypeString"))
                .body("fields[0].alias", is("domain"))
                .body("fields[0].length", is(128))
                .body("fields[1].name", is("average_urltone"))
                .body("fields[1].type", is("esriFieldTypeDouble"))
                .body("fields[1].alias", is("average_urltone"))
                
                .body("features.size()", is(2455))
                .body("features[0].attributes.domain", is("newsbeast.gr"))
                .body("features[0].attributes.average_urltone", is(12.96f))
                
                .body("features[9].attributes.domain", is("camdencourier.com.au"))
                .body("features[9].attributes.average_urltone", is(8.33f))
                
            ;
    	}
    	finally {
    		RestAssured.reset();
    	}
    }
	
}
