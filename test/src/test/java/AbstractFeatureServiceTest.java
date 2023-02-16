import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import org.junit.BeforeClass;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Base class for all tests that exercise Koop endpoints.
 */
public abstract class AbstractFeatureServiceTest {

    @BeforeClass
    public static void connectToNoAuthKoop() {
        RestAssured.port = 8090;
        RestAssured.baseURI = "http://localhost";
        RestAssured.urlEncodingEnabled = false; // we encode the URL parameters manually
    }

    protected final ValidatableResponse getRequest(String path) {
        return new RestAssuredHelper().get(path);
    }

    protected final ValidatableResponse getRequest(String path, int expectedStatusCode) {
        return new RestAssuredHelper().get(path, expectedStatusCode);
    }

    /**
     * @param serviceName
     * @return the start of a path for a request to a Koop server; one intent of this is to make it easier to change
     * all of our tests when the base path changes between Koop versions
     */
    protected final String basePath(String serviceName) {
        return String.format("/marklogic/%s", serviceName);
    }

    protected final String request2path(String requestFile) {
        JsonPath jsonPath = new JsonPath(AbstractFeatureServiceTest.class.getResource("/" + requestFile));

        String url = basePath(jsonPath.getString("params.id")) + "/FeatureServer";

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
                    try {
                        value = URLEncoder.encode(value.toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    url += param + "=" + value.toString() + "&";
                }
            }
        }

        return url;
    }
}
