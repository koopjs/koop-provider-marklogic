import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class CountLayerTest extends AbstractFeatureServiceTest{

	@Test
    public void testGkgCountLayer0() {
        getRequest(request2path("gkgCountLayer0.json"))
                .log().ifValidationFails()
                .body("count", is(38765))
            ;
    }

	@Test
    public void testGkgCountLayer1() {
        getRequest(request2path("gkgCountLayer1.json"))
                .log().ifValidationFails()
                .body("count", is(3557))
            ;
    }


}
