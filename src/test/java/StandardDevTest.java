import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import io.restassured.RestAssured;

public class StandardDevTest  extends AbstractFeatureServiceTest{

	
	@Test
    public void testStddevAndVarUrltone() {

        String path = request2pathWithEncoding("stddevAndVarUrltone.json");
    	RestAssured.urlEncodingEnabled = false;

        try{
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
                .body("fieldAliases.count_urltone", is("count_urltone"))
                .body("fieldAliases.min_urltone", is("min_urltone"))
                .body("fieldAliases.max_urltone", is("max_urltone"))
                .body("fieldAliases.avg_urltone", is("avg_urltone"))
                .body("fieldAliases.stddev_urltone", is("stddev_urltone"))
                .body("fieldAliases.var_urltone", is("var_urltone"))
                
                .body("fields.size()", is(6))
                .body("fields.name", hasItems("count_urltone", "min_urltone", "max_urltone", "avg_urltone", "stddev_urltone", "var_urltone"))
                .body("fields[0].name", is("count_urltone"))
                .body("fields[0].type", is("esriFieldTypeInteger"))
                .body("fields[0].alias", is("count_urltone"))
                .body("fields[5].name", is("var_urltone"))
                .body("fields[5].type", is("esriFieldTypeDouble"))
                .body("fields[5].alias", is("var_urltone"))
                
                .body("features.size()", is(1))
                .body("features[0].attributes.count_urltone", is(38765))
                .body("features[0].attributes.min_urltone", is(-21.77f))
                .body("features[0].attributes.max_urltone", is(16.23f))
                .body("features[0].attributes.avg_urltone", is(-1.1373726299497f))
                .body("features[0].attributes.stddev_urltone", is(3.63348765118955f))
                .body("features[0].attributes.var_urltone", is(13.2022325113469f))
            ;
	}
	finally{
		RestAssured.reset();
	}
	}
	
}
