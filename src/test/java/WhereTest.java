
import org.hamcrest.core.IsNull;
import org.junit.Test;
import io.restassured.RestAssured;
import static org.hamcrest.Matchers.*;


public class WhereTest extends AbstractFeatureServiceTest{

	@Test
    public void testGkgCountWhere() {

        String path = request2path("gkgCountWhere.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(197))
            ;
    }

	@Test
    public void testGkgWhereISNOTNULL() {

        String path = request2path("whereISNOTNULL.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(38765))
            ;
    }

	@Test
    public void testGkgWhereISNULL() {

        String path = request2path("whereISNULL.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(0))
            ;
    }

	@Test
    public void testGkgWhereIn() {

        String path = request2path("gkgWhereIn.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("objectIdFieldName", is("OBJECTID"))
                .body("globalIdFieldName", is(""))
                .body("hasZ", is(false))
                .body("hasM", is(false))

                .body("spatialReference.wkid", is(4326))

                .body("features.size()", is(2))
                .body("features[1].attributes.OBJECTID", is(56576))
                .body("features[1].attributes.urlpubtimedate", is(1495636200000L))
                .body("features[1].attributes.urlpubdate", is(1495584000000L))
                .body("features[1].attributes.url", is("http://www.bendigoadvertiser.com.au/story/4685559/meet-the-real-high-taxpayers-theyre-not-high-earners/"))
                .body("features[1].attributes.name", is("Australia"))
                .body("features[1].attributes.urltone", is(-3.91f))
                .body("features[1].attributes.domain", is("bendigoadvertiser.com.au"))
                .body("features[1].attributes.urllangcode", is("eng"))
                .body("features[1].attributes.geores", is(1))

                .body("features[0].attributes.OBJECTID", is(56577))
                .body("features[0].attributes.urlpubtimedate", is(1495636200000L))
                .body("features[0].attributes.urlpubdate", is(1495584000000L))
                .body("features[0].attributes.url", is("http://www.bendigoadvertiser.com.au/story/4685559/meet-the-real-high-taxpayers-theyre-not-high-earners/"))
                .body("features[0].attributes.name", is("Australia"))
                .body("features[0].attributes.urltone", is(-3.91f))
                .body("features[0].attributes.domain", is("bendigoadvertiser.com.au"))
                .body("features[0].attributes.urllangcode", is("eng"))
                .body("features[0].attributes.geores", is(1))

                .body("exceededTransferLimit", is(false))
            ;
    }


	@Test
    public void testGkgWhereNotIn() {

        String path = request2path("gkgWhereNotIn.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(38763))
            ;
    }

	@Test
    public void testGkgtoDateWhere() {

        String path = request2path("toDateWhere.json");

        RestAssured
        .given()

        .when()
            .log().uri()
            .get(path)

        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()
            .body("count", is(5427))
        ;
    }

    @Test
    public void testOneField() {

        String path = request2path("whereOneField.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("features.size()", is(29))
                .body("features.attributes.domain", everyItem(isOneOf("nikkei.com")))
            ;
    }

    @Test
    public void testOrTwoFields() {

        String path = request2path("whereOr.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("features.size()", is(177))
                .body("features.attributes.domain", everyItem(isOneOf("livetradingnews.com", "nikkei.com")))
            ;
    }

    @Test
    public void testBetweenDates() {

        //System.out.println("Method = " + Thread.currentThread().getStackTrace()[1].getMethodName());

        String path1 = request2path("whereBetweenDates1.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path1)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(33338))
            ;

        String path2 = request2path("whereBetweenDates2.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path2)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(5427))
            ;

    }

    @Test
    public void testBetweenDatesNoMatch() {

        String path = request2path("whereBetweenDatesNoMatch.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(0))
            ;
    }

    @Test
    public void testGreaterThanDate() {

        String path = request2path("whereGreaterThanDate.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(5427))
            ;
    }

    @Test
    public void testGreaterThanTimestamp() {

        String path = request2path("whereGreaterThanTimestamp.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("count", is(33462))
            ;
    }

    @Test
    public void testLike() {
        String path = request2path("whereLike.json");

        RestAssured
            .given()
            .when()
                .log().uri()
                .get(path)

            .then()
                .log().ifError()
                .statusCode(200)
                .log().ifValidationFails()
                .body("features.size()", is(227))
                .body("features.attributes.domain", everyItem(containsString("journal")))
            ;
    }

}

