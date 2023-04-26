
import org.hamcrest.core.IsNull;
import org.junit.Test;
import io.restassured.RestAssured;
import static org.hamcrest.Matchers.*;


public class WhereTest extends AbstractFeatureServiceTest{

	@Test
    public void testGkgCountWhere() {
        getRequest(request2path("gkgCountWhere.json"))
                .body("count", is(197))
            ;
    }

	@Test
    public void testGkgWhereISNOTNULL() {
        getRequest(request2path("whereISNOTNULL.json"))
                .body("count", is(38765))
            ;
    }

	@Test
    public void testGkgWhereISNULL() {
        getRequest(request2path("whereISNULL.json"))
                .body("count", is(0))
            ;
    }

	@Test
    public void testGkgWhereIn() {
        getRequest(request2path("gkgWhereIn.json"))
                .body("objectIdFieldName", is("OBJECTID"))
                .body("globalIdFieldName", is(""))
                .body("hasZ", is(false))
                .body("hasM", is(false))

                .body("spatialReference.wkid", is(4326))

                .body("features.size()", is(2))
                .body("features[1].attributes.OBJECTID", is(56576))
                .body("features[1].attributes.urlpubtimedate", is(1495636200000L))
                .body("features[1].attributes.urlpubdate", is(1495584000000L))
                .body("features[1].attributes.url", is("http://www.bendigoadvertiser.com.au/story/4685559/meet-the-real-high-taxpayers-theyre-not-high-earners/"))
                .body("features[1].attributes.name", is("Australia"))
                .body("features[1].attributes.urltone", is(-3.91f))
                .body("features[1].attributes.domain", is("bendigoadvertiser.com.au"))
                .body("features[1].attributes.urllangcode", is("eng"))
                .body("features[1].attributes.geores", is(1))

                .body("features[0].attributes.OBJECTID", is(56577))
                .body("features[0].attributes.urlpubtimedate", is(1495636200000L))
                .body("features[0].attributes.urlpubdate", is(1495584000000L))
                .body("features[0].attributes.url", is("http://www.bendigoadvertiser.com.au/story/4685559/meet-the-real-high-taxpayers-theyre-not-high-earners/"))
                .body("features[0].attributes.name", is("Australia"))
                .body("features[0].attributes.urltone", is(-3.91f))
                .body("features[0].attributes.domain", is("bendigoadvertiser.com.au"))
                .body("features[0].attributes.urllangcode", is("eng"))
                .body("features[0].attributes.geores", is(1))

                .body("exceededTransferLimit", is(false))
            ;
    }


	@Test
    public void testGkgWhereNotIn() {
        getRequest(request2path("gkgWhereNotIn.json"))
                .body("count", is(38763))
            ;
    }

	@Test
    public void testGkgtoDateWhere() {
        getRequest(request2path("toDateWhere.json"))
            .body("count", is(5427))
        ;
    }

    @Test
    public void testOneField() {
        getRequest(request2path("whereOneField.json"))
                .body("features.size()", is(29))
                .body("features.attributes.domain", everyItem(isOneOf("nikkei.com")))
            ;
    }

    @Test
    public void testOrTwoFields() {
        getRequest(request2path("whereOr.json"))
                .body("features.size()", is(177))
                .body("features.attributes.domain", everyItem(isOneOf("livetradingnews.com", "nikkei.com")))
            ;
    }

    @Test
    public void testBetweenDates() {
        getRequest(request2path("whereBetweenDates1.json"))
                .body("count", is(33338))
            ;
        getRequest(request2path("whereBetweenDates2.json"))
                .body("count", is(5427))
            ;

    }

    @Test
    public void testBetweenDatesNoMatch() {
        getRequest(request2path("whereBetweenDatesNoMatch.json"))
                .body("count", is(0))
            ;
    }

    @Test
    public void testGreaterThanDate() {
        getRequest(request2path("whereGreaterThanDate.json"))
                .body("count", is(5427))
            ;
    }

    @Test
    public void testGreaterThanTimestamp() {
        getRequest(request2path("whereGreaterThanTimestamp.json"))
                .body("count", is(33462))
            ;
    }

    @Test
    public void testLike() {
        getRequest(request2path("whereLike.json"))
                .body("features.size()", is(227))
                .body("features.attributes.domain", everyItem(containsString("journal")))
            ;
    }

}

