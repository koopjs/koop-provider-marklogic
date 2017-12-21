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
                .body("features[0].attributes.OBJECTID", is(7106))
                .body("features[0].attributes.urlpubtimedate", is(1495605600000L))
                .body("features[0].attributes.url", is("http://english.wafa.ps/page.aspx?id=xM2SqLa90913350066axM2SqL"))
                .body("features[0].attributes.name", is("West Bank"))
                .body("features[0].attributes.urltone", is(-1.27F))
                .body("features[0].attributes.domain", is("english.wafa.ps"))
                .body("features[0].attributes.urllangcode", is("eng"))
                .body("features[0].attributes.geores", is(1))
                .body("features[0].geometry.x", is(35.25f))
                .body("features[0].geometry.y", is(31.6667f))
            ;
    }
}
