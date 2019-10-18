import static org.hamcrest.Matchers.is;

import org.hamcrest.core.IsNull;
import org.junit.Test;

import io.restassured.RestAssured;

public class LimitExceedTest extends AbstractFeatureServiceTest{
         
    @Test
    public void testGkgLimitExceed1() {

        String path = request2path("gkgExceededTransferLimit1.json");

        RestAssured
        .given()
        .when()
            .log().uri()
            .get(path)

        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()
            .body("exceededTransferLimit", is(true))
            .body("features.size()", is(5000))
        ;
    }

    @Test
    public void testGkgLimitExceed2() {

        String path = request2path("gkgExceededTransferLimit2.json");

        RestAssured
        .given()
        .when()
            .log().uri()
            .get(path)

        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()
            .body("exceededTransferLimit", is(false))
            .body("features.size()", is(3557))
        ;
    }   

    @Test
    public void testGkgLimitExceed3() {

        String path = request2path("gkgExceededTransferLimit3.json");

        RestAssured
        .given()
        .when()
            .log().uri()
            .get(path)

        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()
            .body("exceededTransferLimit", is(true))
            .body("features.size()", is(3500))
        ;
    } 

    @Test
    public void testGkgLimitExceed4() {

        String path = request2path("gkgExceededTransferLimit4.json");

        RestAssured
        .given()
        .when()
            .log().uri()
            .get(path)

        .then()
            .log().ifError()
            .statusCode(200)
            .log().ifValidationFails()
            .body("exceededTransferLimit", is(true))
            .body("features.size()", is(5))
        ;
    }                                       
}

