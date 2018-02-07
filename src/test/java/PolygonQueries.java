import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import io.restassured.RestAssured;
import static org.hamcrest.Matchers.is;

public class PolygonQueries extends AbstractFeatureServiceTest {

	
	//Wildlife Refuge
	@Test
    public void testSinglePolygonIntersects() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.23714828491211,37.50884073018052],[-122.23302841186523,37.50884073018052],[-122.23302841186523,37.51524053983815],[-122.23714828491211,37.51524053983815],[-122.23714828491211,37.50884073018052]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features[0].attributes.name", is("Wildlife Refuge"))
            ;
		}
	
	//Marklogic neighbourhood, (Holly St)Linestring
	@Test
    public void testSinglePolygonLineStringIntersects1() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.2529411315918,37.52313742082534],[-122.25697517395018,37.52225246712464],[-122.25646018981932,37.51871254735555],[-122.25199699401855,37.51782754117213],[-122.25096702575684,37.52191209752169],[-122.2529411315918,37.52313742082534]]],\"spatialReference\":{\"wkid\":4326 }}","UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(2))
                .body("features[0].attributes.name", is("MarkLogic Neighborhood"))
                .body("features[1].attributes.name", is("Holly St"))
            ;
		}
	
	//Holly ST(LineString) , Airport (Polygon)
	@Test
    public void testSinglePolygonLineStringIntersects2() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.25555896759033,37.51660213066696],[-122.25264072418213,37.51513841952452],[-122.25173950195311,37.51626173528878],[-122.25495815277098,37.51728291676527],[-122.25555896759033,37.51660213066696]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(3))
                .body("features[0].attributes.name", is("MarkLogic Neighborhood"))
                .body("features[1].attributes.name", is("Airport"))
                .body("features[2].attributes.name", is("Holly St"))           
                ;
		}
	
	//Cross two linestrings- Holly ST & Hy 101
	@Test
    public void testTwoLineStringIntersects() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.25622415542603,37.514763977179],[-122.25680351257324,37.513844883456606],[-122.2561812400818,37.513283209498645],[-122.25500106811522,37.51410018840375],[-122.25622415542603,37.514763977179]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(3))
                .body("features[0].attributes.name", is("MarkLogic Neighborhood"))
                .body("features[1].attributes.name", is("Hwy 101"))
                .body("features[2].attributes.name", is("Holly St"))                 ;
		}

	//outside - should not return any feature
	@Test
    public void testOutsidePolygon() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.26568698883058,37.51358957763611],[-122.26609468460083,37.51243218029593],[-122.2649359703064,37.511768370781354],[-122.26360559463501,37.5128747166924],[-122.26568698883058,37.51358957763611]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(0))
            ;
		}
	
	// Only cross outer polygon ( Should return only outer polygon)
	@Test
    public void testOnlyOuterPolygonIntersects() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.26446390151976,37.51570007952023],[-122.26465702056885,37.51522351979558],[-122.26313352584839,37.51472993687259],[-122.2623825073242,37.51587027868436],[-122.26405620574951,37.51626173528878],[-122.26446390151976,37.51570007952023]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features[0].attributes.name", is("MarkLogic Neighborhood"))
                ;
		}
	
	//Only covering one point (Should return the point and Outer polygon)
	@Test
    public void testOnlyCoveringPointIntersects() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.25903511047362,37.513998066529716],[-122.25912094116212,37.51290875784499],[-122.25779056549074,37.512534304312624],[-122.2571897506714,37.51358957763611],[-122.25903511047362,37.513998066529716]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(2))
                .body("features[0].attributes.name", is("Restaurant"))
                .body("features[1].attributes.name", is("MarkLogic Neighborhood"))
                ;
		}
	}
