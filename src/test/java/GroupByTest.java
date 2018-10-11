import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasItems;

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
                .body("fields.name", hasItems("domain", "domain_count"))
                .body("fields.type", hasItems("esriFieldTypeString", "esriFieldTypeInteger"))

                .body("features.size()", is(2455))
                .body("features[0].attributes.domain", is("fax.al"))
                .body("features[0].attributes.domain_count", is(1259))

                .body("features[9].attributes.domain", is("entornointeligente.com"))
                .body("features[9].attributes.domain_count", is(199))
            ;
    }

    @Test
    public void testGroupByTwoFields() {

        String path = request2path("groupByTwoFields.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("features.size()", is(9061))
                .body(
                    "features.attributes.find { it.domain == '1057fmthefan.com' & it.name == 'Chile' }.objectid_count",
                    is(5)
                )
                .body(
                    "features.attributes.find { it.domain == '4-traders.com' & it.name == 'Japan' }.objectid_count",
                    is(8)
                )
                .body(
                    "features.attributes.find { it.domain == '9news.com.au' & it.name == 'Australia' }.objectid_count",
                    is(12)
                )
                .body(
                    "features.attributes.find { it.domain == '9news.com.au' & it.name == 'New Zealand' }.objectid_count",
                    is(1)
                )
            ;
    }

    @Test
    public void testGroupByOrderByCount() {

        String path = request2path("groupByOrderByCount.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("features.size()", is(2455))
                .body("features[0].attributes.domain", is("fax.al"))
                .body("features[0].attributes.objectid_count", is(1259))
            ;
    }


    @Test
    public void testGroupByWithFilter() {

        String path = request2path("groupByWithFilter.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("features.size()", is(2))
                .body("features.attributes.find { it.domain == '4-traders.com' }.objectid_count", is(178))
                .body("features.attributes.find { it.domain == '9news.com.au' }.objectid_count", is(14))
            ;
    }

}
