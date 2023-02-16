import java.io.UnsupportedEncodingException;

import org.json.simple.parser.ParseException;
import org.junit.Test;
import io.restassured.RestAssured;
import static org.hamcrest.Matchers.is;

public class DataTypes extends AbstractFeatureServiceTest {

    @Test
    public void testDefaultStringLength() throws UnsupportedEncodingException, ParseException  {

        String path = basePath("GDeltGKG") + "/FeatureServer/{layer}";
        RestAssured.urlEncodingEnabled = false;

        RestAssured
            .given()
              .pathParam("layer", 3)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("fields.find { it.name == 'domain' }.length", is(1024))
            ;
    }

}
