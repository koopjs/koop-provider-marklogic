import io.restassured.RestAssured;
import org.hamcrest.core.IsNull;
import org.junit.Test;

import static org.hamcrest.Matchers.*;


public class ServiceDescriptorArrayTest extends AbstractFeatureServiceTest {
    @Test
    public void testDataSourceWithViewAsRoot() {
        String path = request2path("DataSourceArrayExampleService.json");

        RestAssured
                .given()
                .when()
                    .log().uri()
                    .get(path)

                .then()
                    .log().ifError()
                    .statusCode(200)
                    .log().ifValidationFails()

                    .body("features.size()", is(5))

                    .body("features[0].attributes.OBJECTID", is(1))
                    .body("features[0].attributes.urlpubtimedate", is(1495605600000l))
                    .body("features[0].attributes.urlpubdate", is(1495584000000l))
                    .body("features[0].attributes.url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
                    .body("features[0].attributes.name", is("United Kingdom"))
                    .body("features[0].attributes.urltone", is(-3.41f))
                    .body("features[0].attributes.domain", is("rtbf.be"))
                    .body("features[0].attributes.urllangcode", is("fra"))
                    .body("features[0].attributes.geores", is(1))
        ;
    }

    @Test
    public void testDataSourceWithViewAsRootAndSparqlJoin() {
        String path = request2path("DataSourceArrayViewPlusSparqlJoin.json");

        RestAssured
                .given()
                .when()
                .log().uri()
                .get(path)

                .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()

                .body("features.size()", is(5))

                .body("features[0].attributes.OBJECTID", is(1))
                .body("features[0].attributes.url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
                .body("features[0].attributes.OBJECT_ID", is(1))
                .body("features[0].attributes.sparql_url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
        ;
    }

    @Test
    public void testDataSourceWithSparqlAsRoot() {
        String path = request2path("DataSourceArraySparqlOnly.json");

        RestAssured
                .given()
                .when()
                .log().uri()
                .get(path)

                .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()

                .body("features.size()", is(5))

                .body("features[0].attributes.OBJECT_ID", is(1))
                .body("features[0].attributes.sparql_url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
        ;
    }
}
