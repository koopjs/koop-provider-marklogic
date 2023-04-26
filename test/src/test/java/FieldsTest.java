import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


public class FieldsTest extends AbstractFeatureServiceTest {

    @Test
    public void testAllFields() {
        getRequest(request2path("gkgAllFields.json"))
                .body("features.size()", is(5))
                .body("features[0].attributes.OBJECTID", notNullValue())
                .body("features[0].attributes.urlpubtimedate", notNullValue())
                .body("features[0].attributes.urlpubdate", notNullValue())
                .body("features[0].attributes.url", notNullValue())
                .body("features[0].attributes.name", notNullValue())
                .body("features[0].attributes.urltone", notNullValue())
                .body("features[0].attributes.domain", notNullValue())
                .body("features[0].attributes.urllangcode", notNullValue())
                .body("features[0].attributes.geores", notNullValue())
                .body("features[0].geometry.x", notNullValue())
                .body("features[0].geometry.y", notNullValue())
            ;
    }
}
