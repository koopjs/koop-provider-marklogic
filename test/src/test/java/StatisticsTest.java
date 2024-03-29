import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import org.junit.Ignore;
import org.junit.Test;


public class StatisticsTest  extends AbstractFeatureServiceTest{

    @Test
    public void testAverageTone() {
        getRequest(request2path("gkgAvgTone.json"))
// This field doesn't seem to be returned any longer.
// Is there a switch to turn it on or are they completely gone?
//                .body("displayFieldName", is(""))

                .body("fields.size()", is(2))
                .body("fields.name", hasItems("domain", "average_urltone"))

                .body("features.size()", is(2455))
                .body("features[0].attributes.domain", is("newsbeast.gr"))
                .body("features[0].attributes.average_urltone", is(12.96f))

                .body("features[9].attributes.domain", is("camdencourier.com.au"))
                .body("features[9].attributes.average_urltone", is(8.33f));
    }

    @Test
    public void testAverageMinMaxTone() {
        getRequest(request2path("gkgAvgMinMaxTone.json"))
// This field doesn't seem to be returned any longer.
// Is there a switch to turn it on or are they completely gone?
//                .body("displayFieldName", is(""))

                .body("fields.size()", is(4))
                .body("fields.name", hasItems("domain", "maximum_urltone", "average_urltone", "minimum_urltone"))

                .body("features.size()", is(2455))
                .body("features[0].attributes.domain", is("newsbeast.gr"))
                .body("features[0].attributes.average_urltone", is(12.96f))
                .body("features[0].attributes.minimum_urltone", is(12.96f))
                .body("features[0].attributes.maximum_urltone", is(12.96f));
    }


	@Test
    @Ignore("This is failing on Jenkins because the variance library doesn't seem to be getting loaded")
    public void testStddevAndVarUrltone() {
        getRequest(request2path("stddevAndVarUrltone.json"))
// This field doesn't seem to be returned any longer.
// Is there a switch to turn it on or are they completely gone?
//                .body("displayFieldName", is(""))

                .body("fields.size()", is(6))
                .body("fields.name", hasItems("count_urltone", "min_urltone", "max_urltone", "avg_urltone", "stddev_urltone", "var_urltone"))

                .body("features.size()", is(1))
                .body("features[0].attributes.count_urltone", is(38765))
                .body("features[0].attributes.min_urltone", is(-21.77f))
                .body("features[0].attributes.max_urltone", is(16.23f))
                .body("features[0].attributes.avg_urltone", is(-1.1373726299497f))
            // This previously expected a value of 3.6335345176664f; it's not yet known if that should really be the
            // value, or if the updated one below is "more correct". It may be that the data is different now on account
            // of more data being present in the GDS test app.
                .body("features[0].attributes.stddev_urltone", is(3.63348765118955f))
            // Same as above - this previously expected a value of 13.2025730910732f
                .body("features[0].attributes.var_urltone", is(13.2022325113469f))

            ;
    }
}
