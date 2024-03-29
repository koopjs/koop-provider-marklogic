import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.simple.parser.ParseException;
import org.junit.Test;

public class PointQueries  extends AbstractFeatureServiceTest {

	//testOnePolygonIntersects
	@Test
    public void testPointIntersects1() throws UnsupportedEncodingException, ParseException  {

        String path = basePath("GDeltGKG") + "/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"x\" : -122.25972175598143, \"y\" : 37.51871254735555}" ,"UTF-8");

        RestAssuredHelper helper = new RestAssuredHelper();
        helper.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPoint")
            	.pathParam("geometry",GeometryEncoded);

        helper.get(path)
                .body("features.size()", is(1))
                .body("features[0].attributes.name", is("MarkLogic Neighborhood"))
            ;
		}

	//testTwoPolygonIntersects
	@Test
    public void testPointIntersects2() throws UnsupportedEncodingException, ParseException  {

        String path = basePath("GDeltGKG") + "/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"x\" : -122.24564552307129, \"y\" : 37.513198107015064}" ,"UTF-8");

        RestAssuredHelper helper = new RestAssuredHelper();
        helper.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPoint")
            	.pathParam("geometry",GeometryEncoded);

        helper.get(path)
                .body("features.size()", is(2))
                .body("features.attributes.name", hasItems("Wildlife Refuge","MarkLogic Neighborhood"));
		}


	//Inside single polygon Expected- WildLife Refuge
	@Test
    public void testPointContains1() throws UnsupportedEncodingException, ParseException  {

        String path = basePath("GDeltGKG") + "/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
		String GeometryEncoded = URLEncoder.encode("{\"x\" :-122.2411823272705, \"y\" : 37.50918115940604}" ,"UTF-8");

        RestAssuredHelper helper = new RestAssuredHelper();
        helper.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPoint")
            	.pathParam("geometry",GeometryEncoded)
                .pathParam("spatialRel","esrispatialrelcontains");

        helper.get(path)
                .body("features.size()", is(1))
                .body("features[0].attributes.name", is("Wildlife Refuge"));
		}

	//Inside two polygon Expected- WildLife Refuge
		@Test
	    public void testPointContains2() throws UnsupportedEncodingException, ParseException  {

        String path = basePath("GDeltGKG") + "/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
			String GeometryEncoded = URLEncoder.encode("{\"x\" :-122.25208282470703, \"y\" : 37.51571709945411}" ,"UTF-8");

        RestAssuredHelper helper = new RestAssuredHelper();
        helper.pathParam("layer", 3)
	            	.pathParam("geometryType", "esriGeometryPoint")
	            	.pathParam("geometry",GeometryEncoded)
	                .pathParam("spatialRel","esrispatialrelcontains");

        helper.get(path)
	                .body("features.size()", is(2))
	                .body("features.attributes.name", hasItems("Airport","MarkLogic Neighborhood"));
			}

		//External Point Expected- No Features
				@Test
			    public void testPointContains3() throws UnsupportedEncodingException, ParseException  {

        String path = basePath("GDeltGKG") + "/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"x\" :-122.24152565002441, \"y\" : 37.52068675409422}" ,"UTF-8");

        RestAssuredHelper helper = new RestAssuredHelper();
        helper.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPoint")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esrispatialrelcontains");

        helper.get(path)
			                .body("features.size()", is(0));
			    	}
}
