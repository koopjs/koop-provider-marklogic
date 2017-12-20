import org.junit.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static org.hamcrest.Matchers.*;


public class FieldsTest extends AbstractFeatureServiceTest {

    @Test
    public void testAllFields() {

        String path = request2path("gkgAllFields.json");

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
                .body("features[0].attributes.OBJECTID", is(1483))
                .body("features[0].attributes.urlpubtimedate", is(1495605600000L))
                .body("features[0].attributes.url", is("http://www.ccdy.cn/./xinwen/jiaoliu/xinwen/201705/t20170524_1329138.htm"))
                .body("features[0].attributes.name", is("China"))
                .body("features[0].attributes.urltone", is(4.07F))
                .body("features[0].attributes.domain", is("ccdy.cn"))
                .body("features[0].attributes.urllangcode", is("zho"))
                .body("features[0].attributes.geores", is(1))
                .body("features[0].geometry.x", is(105))
                .body("features[0].geometry.y", is(35))
            ;
    }
}
