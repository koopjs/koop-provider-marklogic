import static org.hamcrest.Matchers.is;

import org.junit.Test;

import io.restassured.RestAssured;

public class GroupByTest extends AbstractFeatureServiceTest{

	@Test
    public void testGkgGroupBy() {

        String path = request2path("gkgGroupBy.json");

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
                .body("fieldAliases.domain_count", is("domain_count"))
                
                .body("fields.size()", is(2))
                .body("fields[0].name", is("domain"))
                .body("fields[0].type", is("esriFieldTypeString"))
                .body("fields[0].alias", is("domain"))
                .body("fields[0].length", is(128))
                .body("fields[1].name", is("domain_count"))
                .body("fields[1].type", is("esriFieldTypeInteger"))
                .body("fields[1].alias", is("domain_count"))
                
                .body("features.size()", is(2123))
                .body("features[0].attributes.domain", is("fax.al"))
                .body("features[0].attributes.domain_count", is(569))
                
                .body("features[9].attributes.domain", is("moonofalabama.org"))
                .body("features[9].attributes.domain_count", is(196))
            ;
    }
	
}
