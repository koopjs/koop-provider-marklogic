import java.io.UnsupportedEncodingException;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class DataTypes extends AbstractFeatureServiceTest {

    @Test
    public void testDefaultStringLength() throws UnsupportedEncodingException, ParseException  {

        String path = basePath("GDeltGKG") + "/FeatureServer/{layer}";

        RestAssuredHelper helper = new RestAssuredHelper();
        helper.pathParam("layer", 3);

        helper.get(path)
                .body("fields.find { it.name == 'domain' }.length", is(1024))
            ;
    }

}
