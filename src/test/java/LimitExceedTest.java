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
        ;
    }                            
}

