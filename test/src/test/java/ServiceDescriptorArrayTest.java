import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.is;


public class ServiceDescriptorArrayTest extends AbstractFeatureServiceTest {
    @Test
    public void testViewAsRoot() {
        getRequest(request2path("ViewPlusSparqlJoin.json"))
                .body("features.size()", is(5))

                .body("features[0].attributes.OBJECTID", is(1))
                .body("features[0].attributes.url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
                .body("features[0].attributes.OBJECT_ID", is(1))
                .body("features[0].attributes.sparql_url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
        ;
    }

    @Test
    public void testDataSourceWithViewAsRoot() {
        getRequest(request2path("DataSourceArrayExampleService.json"))
                    .body("features.size()", is(5))

                    .body("features[0].attributes.OBJECTID", is(1))
                    .body("features[0].attributes.urlpubtimedate", is(1495605600000l))
                    .body("features[0].attributes.urlpubdate", is(1495584000000l))
                    .body("features[0].attributes.url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
                    .body("features[0].attributes.name", is("United Kingdom"))
                    .body("features[0].attributes.urltone", is(-3.41f))
                    .body("features[0].attributes.domain", is("rtbf.be"))
                    .body("features[0].attributes.urllangcode", is("fra"))
                    .body("features[0].attributes.geores", is(1))
        ;
    }

    @Test
    public void testDataSourceWithViewAsRootAndSparqlJoin() {
        getRequest(request2path("DataSourceArrayViewPlusSparqlJoin.json"))
                .body("features.size()", is(5))

                .body("features[0].attributes.OBJECTID", is(1))
                .body("features[0].attributes.url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
                .body("features[0].attributes.OBJECT_ID", is(1))
                .body("features[0].attributes.sparql_url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
        ;
    }

    @Test
    public void testDataSourceWithSparqlAsRoot() {
        getRequest(request2path("DataSourceArraySparqlOnly.json"))
                .body("features.size()", is(5))

                .body("features[0].attributes.OBJECTID", is(0))
                .body("features[0].attributes.sparql_url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
                .body("features[0].attributes.DocId", is("/gkg_geojson/gkg_geojson_2017_05_24T02_26_02.zip/gkg_geojson_2017_05_24T02_26_02/0.json"))
        ;
    }

    @Test
    @Ignore("Temporarily ignored, need some help with debugging why this query fails")
    public void testDataSourceWithSparqlAsRootAndViewJoinWithFieldsElement() {
        getRequest(request2path("DataSourceArraySparqlPlusViewJoinWithFieldsElement.json"))
                .body("features.size()", is(5))

                .body("features[0].attributes.OBJECTID", is(0))
                .body("features[0].attributes.url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
                .body("features[0].attributes.OBJECT_ID", is(0))
                .body("features[0].attributes.sparql_url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
        ;
    }

    @Test
    @Ignore("Temporarily ignored, need some help with debugging why this query fails")
    public void testDataSourceWithSparqlAsRootAndViewJoinWithoutFieldsElement() {
        getRequest(request2path("DataSourceArraySparqlPlusViewJoinWithoutFieldsElement.json"))
                .body("features.size()", is(5))

                .body("features[0].attributes.OBJECT_ID", is(0))
                .body("features[0].attributes.sparql_url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))

                .body("features[0].attributes.OBJECTID", is(0))
                .body("features[0].attributes.urlpubtimedate", is(1495605600000l))
                .body("features[0].attributes.urlpubdate", is(1495584000000l))
                .body("features[0].attributes.url", is("https://www.rtbf.be/info/monde/detail_le-suspect-de-manchester-un-etudiant-reserve-issu-d-un-quartier-modeste?id=9615589"))
                .body("features[0].attributes.name", is("Tripoli, Tarabulus, Libya"))
                .body("features[0].attributes.urltone", is(-3.41f))
                .body("features[0].attributes.domain", is("rtbf.be"))
                .body("features[0].attributes.urllangcode", is("fra"))
                .body("features[0].attributes.geores", is(3))
        ;
    }

    @Test
    public void testDataSourceWithSparqlAsRootAndViewJoinWithoutFieldsElementStats() {
        getRequest(request2path("DataSourceArraySparqlPlusViewJoinWithoutFieldsElementStats.json"))
                .body("features.size()", is(2))
                .body(
                    "features.attributes.find { it.domain == '4-traders.com' }.objectid_count",
                    is(8)
                )
                .body(
                    "features.attributes.find { it.domain == 'bendigoadvertiser.com.au' }.objectid_count",
                    is(1)
                )
        ;
    }
}
