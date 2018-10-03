import io.restassured.RestAssured;
import org.hamcrest.core.IsNull;
import org.junit.Test;

import static org.hamcrest.Matchers.*;


public class ServiceDescriptorArrayTest extends AbstractFeatureServiceTest {
    @Test
    public void testDataSources() {
        String path = request2path("GDeltExampleService.json");

        RestAssured
                .given()
                .when()
                .log().uri()
                .get(path)

                .then()
                .log().ifError()
                .statusCode(200)
        ;
    }
}


