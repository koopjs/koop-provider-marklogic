import static org.hamcrest.Matchers.is;

import org.junit.Test;

import io.restassured.RestAssured;

public class PubtimeErrorTest extends AbstractFeatureServiceTest{

	@Test
    public void testGkgPubtimeError0() {
        getRequest(request2path("gkgPubtimeError.json"))
	            .body("count", is(0))
                ;
    }
}
