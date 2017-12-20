import org.junit.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static org.hamcrest.Matchers.*;


public class WhereTest extends AbstractFeatureServiceTest {

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
}
