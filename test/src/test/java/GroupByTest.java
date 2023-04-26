import org.junit.Test;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class GroupByTest extends AbstractFeatureServiceTest{

    @Test
    public void testGkgGroupBy() {
        getRequest(request2path("gkgGroupBy.json"))
// This field doesn't seem to be returned any longer.
// Is there a switch to turn it on or are they completely gone?
//                .body("displayFieldName", is(""))

                .body("fields.size()", is(2))
                .body("fields.name", hasItems("domain", "domain_count"))
                .body("fields.type", hasItems("esriFieldTypeString", "esriFieldTypeDouble"))
                .body("fields.alias", hasItems("domain", "domain_count"))

                .body("features.size()", is(2455))
                .body("features[0].attributes.domain", is("fax.al"))
                .body("features[0].attributes.domain_count", is(1259))

                .body("features[9].attributes.domain", is("entornointeligente.com"))
                .body("features[9].attributes.domain_count", is(199))
            ;
    }

    @Test
    public void testGroupByTwoFields() {
        getRequest(request2path("groupByTwoFields.json"))
                .body("features.size()", is(9061))
                .body(
                    "features.attributes.find { it.domain == '1057fmthefan.com' & it.name == 'Chile' }.objectid_count",
                    is(5)
                )
                .body(
                    "features.attributes.find { it.domain == '4-traders.com' & it.name == 'Japan' }.objectid_count",
                    is(8)
                )
                .body(
                    "features.attributes.find { it.domain == '9news.com.au' & it.name == 'Australia' }.objectid_count",
                    is(12)
                )
                .body(
                    "features.attributes.find { it.domain == '9news.com.au' & it.name == 'New Zealand' }.objectid_count",
                    is(1)
                )
            ;
    }

    @Test
    public void testGroupByOrderByCount() {
        getRequest(request2path("groupByOrderByCount.json"))
                .body("features.size()", is(2455))
                .body("features[0].attributes.domain", is("fax.al"))
                .body("features[0].attributes.objectid_count", is(1259))
            ;
    }


    @Test
    public void testGroupByWithFilter() {
        getRequest(request2path("groupByWIthFilter.json"))
                .body("features.size()", is(2))
                .body("features.attributes.find { it.domain == '4-traders.com' }.objectid_count", is(178))
                .body("features.attributes.find { it.domain == '9news.com.au' }.objectid_count", is(14))
            ;
    }

}
