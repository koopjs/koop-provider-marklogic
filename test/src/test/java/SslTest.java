import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLHandshakeException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;


public class SslTest extends AbstractFeatureServiceTest {

    @Before
    public void connectToSslPort() {
        RestAssured.port = 443;
        RestAssured.baseURI = "https://localhost";
    }

    @Test
    public void getServiceDescriptor() {
        getRelaxedHTTPSValidationRequest(request2path("featureService.json"))
            .body("serviceDescription", notNullValue());
    }
    @Test
    public void withoutRelaxedHTTPSValidation() {
        SSLHandshakeException ex = assertThrows(SSLHandshakeException.class,
            () -> getRequest(request2path("featureService.json")));

        assertTrue(
            "The request should fail because the Koop server requires SSL, and it's using a self-signed cert that " +
                "the underlying JVM is not expected to trust; actual message: " + ex.getMessage(),
            ex.getMessage().contains("PKIX path building failed")
        );
    }}


