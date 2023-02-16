import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class ClassificationDefTest extends AbstractFeatureServiceTest{

    @Test
    public void testGkgToneStandardDeviation0() {
        getRequest(request2path("gkgToneStandardDeviation0.json"))
                .body("classBreakInfos[0].classMinValue", is(-13340.0579808794f))
                .body("classBreakInfos[0].classMaxValue", is(3183.36201912057f))
                .body("minValue", is(-13340.0579808794f))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(5))
            ;
    }

    @Test
    public void testGkgToneStandardDeviation1() {
        getRequest(request2path("gkgToneStandardDeviation1.json"))
                .body("classBreakInfos[0].classMinValue", is(-61804.899857712f))
                .body("classBreakInfos[0].classMaxValue", is(-44763.169857712f))
                .body("minValue", is(-61804.899857712f))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(10))
            ;
    }

    @Test
    public void testGkgToneStandardDeviation2() {
        getRequest(request2path("gkgToneStandardDeviation2.json"))
                .body("classBreakInfos[0].classMinValue", is(-141140.379126623f))
                .body("classBreakInfos[0].classMaxValue", is(-124345.619126623f))
                .body("minValue", is(-141140.379126623f))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(20))
            ;
    }

    @Test
    public void gkgObjectIdGeometricInterval0() {
        getRequest(request2path("gkgObjectIdGeometricInterval0.json"))
                .body("classBreakInfos[0].classMinValue", is(1))
                .body("classBreakInfos[0].classMaxValue", is(2.99261807854486f))
                .body("minValue", is(1))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(10))

            ;
    }

    @Test
    public void gkgObjectIdGeometricInterval1() {
        getRequest(request2path("gkgObjectIdGeometricInterval1.json"))
                .body("classBreakInfos[0].classMinValue", is(37))
                .body("classBreakInfos[0].classMaxValue", is(160.936078759448f))
                .body("minValue", is(37))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(5))
            ;
    }

    @Test
    public void gkgObjectIdGeometricInterval2() {
        getRequest(request2path("gkgObjectIdGeometricInterval1.json"))
                .body("classBreakInfos[0].classMinValue", is(37))
                .body("classBreakInfos[0].classMaxValue", is(160.936078759448f))
                .body("minValue", is(37))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(5))
            ;
    }

    @Test
    public void gkgObjectIdQuantile0() {
        getRequest(request2path("gkgObjectIdQuantile0.json"))
                .body("classBreakInfos[0].classMinValue", is(1))
                .body("classBreakInfos[0].classMaxValue", is(6437))
                .body("minValue", is(1))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(10))
            ;
    }


    @Test
    public void gkgObjectIdQuantile1 () {
        getRequest(request2path("gkgObjectIdQuantile1.json"))
                .body("classBreakInfos[0].classMinValue", is(37))
                .body("classBreakInfos[0].classMaxValue", is(7805))
                .body("minValue", is(37))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(5))
            ;
    }

    @Test
    public void gkgObjectIdQuantile2 () {
        getRequest(request2path("gkgObjectIdQuantile2.json"))
                .body("classBreakInfos[0].classMinValue", is(0))
                .body("classBreakInfos[0].classMaxValue", is(3042))
                .body("minValue", is(0))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(20))
            ;
    }

    @Test
    public void gkgToneEqualInterval0 () {
        getRequest(request2path("gkgToneEqualInterval0.json"))
                .body("classBreakInfos[0].classMinValue", is(-21.77f))
                .body("classBreakInfos[0].classMaxValue", is(-14.17f))
                .body("minValue", is(-21.77f))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(5))
            ;
    }


    @Test
    public void gkgToneEqualInterval1 () {
        getRequest(request2path("gkgToneEqualInterval1.json"))
                .body("classBreakInfos[0].classMinValue", is(-15.72f))
                .body("classBreakInfos[0].classMaxValue", is(-13.206f))
                .body("minValue", is(-15.72f))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(10))
            ;
    }

    @Test
    public void gkgToneEqualInterval2 () {
        getRequest(request2path("gkgToneEqualInterval2.json"))
                .body("classBreakInfos[0].classMinValue", is(-21.77f))
                .body("classBreakInfos[0].classMaxValue", is(-19.87f))
                .body("minValue", is(-21.77f))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(20))
            ;
    }

    @Test
    public void gkgToneQuantile0 () {
        getRequest(request2path("gkgToneQuantile0.json"))
                .body("classBreakInfos[0].classMinValue", is(-21.77f))
                .body("classBreakInfos[0].classMaxValue", is(-8.48f))
                .body("minValue", is(-21.77f))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(10))
            ;
    }


    @Test
    public void gkgToneQuantile1 () {
        getRequest(request2path("gkgToneQuantile1.json"))
                .body("classBreakInfos[0].classMinValue", is(-15.72f))
                .body("classBreakInfos[0].classMaxValue", is(-4.64f))
                .body("minValue", is(-15.72f))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(5))
            ;
    }


    @Test
    public void gkgToneQuantile2 () {
        getRequest(request2path("gkgToneQuantile2.json"))
                .body("classBreakInfos[0].classMinValue", is(-21.77f))
                .body("classBreakInfos[0].classMaxValue", is(-10.82f))
                .body("minValue", is(-21.77f))
                .body("type", is("classBreaks"))
                .body("classBreakInfos.size()", is(20))
            ;
    }
}
