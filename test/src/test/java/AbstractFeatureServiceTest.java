import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;


public abstract class AbstractFeatureServiceTest {

    @BeforeClass
    public static void connectToNoAuthKoop() {
        RestAssured.port = 8090;
        RestAssured.baseURI = "http://localhost";
        RestAssured.urlEncodingEnabled = false; // we encode the URL parameters manually
    }

    protected final ValidatableResponse getRequest(String path) {
        return RestAssured.given()
                   .when()
                   // Logging the URI each time seems useful as it varies across tests
                   .log().uri()
                   .get(path)
                   .then()
                   .log().ifError()
                   .log().ifValidationFails();
    }

    public static String request2path(String requestFile) {
        return request2path(requestFile, true);
    }

    public static String request2path(String requestFile, boolean urlEncode) {
        JsonPath jsonPath = new JsonPath(AbstractFeatureServiceTest.class.getResource("/" + requestFile));

        String service = jsonPath.getString("params.id");
        String url = "/marklogic/" + service + "/FeatureServer";

        String layer = jsonPath.getString("params.layer");
        if (layer != null) {
            url += "/" + layer;
        }

        String method = jsonPath.getString("params.method");
        if (method != null) {
            url += "/" + method;
        }

        Map<String, Object> query = jsonPath.getJsonObject("query");
        if (query != null) {
            url += "?";
            for (String param : query.keySet()) {

                Object value = query.get(param);
                if (value != null) {
                    if (urlEncode) {
                        try {
                            value = URLEncoder.encode(value.toString(), "UTF-8");
                        } catch(UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    url += param + "=" + value.toString() + "&";
                }
            }
        }

        return url;
    }
}
