import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import io.restassured.RestAssured;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.*;

public class PolygonQueries extends AbstractFeatureServiceTest {
	
    //===============================Intersect=============================================

	//Wildlife Refuge testSinglePolygonIntersects
	@Test
    public void testPolygonIntersects1() throws UnsupportedEncodingException, ParseException  {

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
	
	//testSinglePolygonLineStringIntersects1 Expected : Marklogic neighbourhood, (Holly St)Linestring
	@Test
    public void testPolygonIntersects2() throws UnsupportedEncodingException, ParseException  {

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
                .body("features.attributes.name", hasItems("Holly St","MarkLogic Neighborhood"))            ;
		}
	
	//testSinglePolygonLineStringIntersects2 Expected : Holly ST(LineString) , Airport (Polygon)
	@Test
    public void testPolygonIntersects3() throws UnsupportedEncodingException, ParseException  {

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
                .body("features.attributes.name", hasItems("Airport","Holly St","MarkLogic Neighborhood"))            ;                       
                
		}
	
	//testTwoLineStringIntersects Expected : Cross two linestrings- Holly ST & Hy 101
	@Test
    public void testPolygonIntersects4() throws UnsupportedEncodingException, ParseException  {

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
                .body("features.attributes.name", hasItems("Hwy 101","Holly St","MarkLogic Neighborhood"))     
            ;
		}

	//outside - should not return any feature
	@Test
    public void testPolygonIntersects5() throws UnsupportedEncodingException, ParseException  {

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
    public void testPolygonIntersects6() throws UnsupportedEncodingException, ParseException  {

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
    public void testPolygonIntersects7() throws UnsupportedEncodingException, ParseException  {

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
                .body("features.attributes.name", hasItems("Restaurant","MarkLogic Neighborhood"))            ;                                                       
		}

    //================================Within=======================================

	//Polygon1 ( Features within Polygon )  Expected : Restaurant 
	@Test
    public void testPolygonWithin1() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.25903511047362,37.513998066529716],[-122.25912094116212,37.51290875784499],[-122.25779056549074,37.512534304312624],[-122.2571897506714,37.51358957763611],[-122.25903511047362,37.513998066529716]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
                .pathParam("spatialRel","esriSpatialRelWithin")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features[0].attributes.name", is("Restaurant"))            ;                                                       
		}
	
	
	//Polygon2 ( Features within Polygon )  Expected : Airport 
	@Test
    public void testPolygonWithin2() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.2487354,37.5181339],[-122.250967,37.5177254],[-122.2537994,37.5157171],[-122.2543144,37.5148321],[-122.2466755,37.5083982],[-122.2457314,37.5086705],[-122.2481775,37.5121769],[-122.2487354,37.5181339]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
                .pathParam("spatialRel","esriSpatialRelWithin")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(1))
                .body("features[0].attributes.name", is("Airport"))            ;                                                       
		}
	
	//Polygon3 ( Features within Polygon - Around MarkLogic NH)  Expected : 6 features 
	@Test
    public void testPolygonWithin3() throws UnsupportedEncodingException, ParseException  {

		String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
		String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.2634554,37.519087],[-122.2633696,37.5134364],[-122.2598076,37.5061853],[-122.2455597,37.5033596],[-122.2446156,37.512279],[-122.2480488,37.5212994],[-122.2634554,37.519087]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
		RestAssured.urlEncodingEnabled = false;
 		
    	RestAssured
            .given()
            	.pathParam("layer", 3)
            	.pathParam("geometryType", "esriGeometryPolygon")
            	.pathParam("geometry",GeometryEncoded)
                .pathParam("spatialRel","esriSpatialRelWithin")
            .when()
                .log().uri()
                .get(path)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("features.size()", is(6))
                .body("features.attributes.name", hasItems("MarkLogic Neighborhood","Restaurant","Holly St","Airport","Museum","MarkLogic HQ"))            ;                                                       
                ;                                                       
		}
	
	//Polygon4 ( No Features within Polygon)  Expected : Zero results 
		@Test
	    public void testPolygonWithin4() throws UnsupportedEncodingException, ParseException  {

			String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
			String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.2428131,37.517351],[-122.2468472,37.5131641],[-122.2422123,37.5069683],[-122.2356892,37.5102365],[-122.2384787,37.5154788],[-122.2428131,37.517351]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
			RestAssured.urlEncodingEnabled = false;
	 		
	    	RestAssured
	            .given()
	            	.pathParam("layer", 3)
	            	.pathParam("geometryType", "esriGeometryPolygon")
	            	.pathParam("geometry",GeometryEncoded)
	                .pathParam("spatialRel","esriSpatialRelWithin")
	            .when()
	                .log().uri()
	                .get(path)
	            .then()
	                .log().ifError()
	                .statusCode(200)
	                .body("features.size()", is(0))
	                ;                                                       
			}
		
		//Polygon5 (Features within Polygon - WIldLife refuge)  Expected : WildLife refuge
				@Test
			    public void testPolygonWithin5() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.24058151245116,37.51827004557588],[-122.2474479675293,37.51748715138366],[-122.24873542785645,37.51159816226253],[-122.24259853363036,37.50567466402333],[-122.23311424255371,37.50839816986626],[-122.2344446182251,37.515104379388916],[-122.24058151245116,37.51827004557588]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolygon")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esriSpatialRelWithin")
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
				
				//Polygon6 (Reverse Test -Intersecting wildlife refuge - not within )  Expected : Zero results
				@Test
			    public void testPolygonWithin6() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.23937988281251,37.51748715138366],[-122.24380016326903,37.514389532954716],[-122.24637508392334,37.50999818321275],[-122.24092483520508,37.505061861515316],[-122.23294258117676,37.50979392809947],[-122.23937988281251,37.51748715138366]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolygon")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esriSpatialRelWithin")
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(0))			                ;                                                       
					}
				
				//Polygon7 (Reverse Test -External polygon - Not related to any feature )  Expected : Zero results
				@Test
			    public void testPolygonWithin7() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.23491668701172,37.51704464233734],[-122.23637580871582,37.5151724596446],[-122.23470211029051,37.51295981954475],[-122.23011016845702,37.5140491274842],[-122.23066806793211,37.515649019695296],[-122.23491668701172,37.51704464233734]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolygon")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esriSpatialRelWithin")
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(0))			                ;                                                       
					}
				
//=============================================Contains====================================================		
				
				//Polygon1 Contains (Inside single polygon )  Expected : WildLife refuge
				@Test
			    public void testPolygonContains1() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.2418689727783,37.513810842731],[-122.24289894104004,37.511155618297025],[-122.24032402038574,37.51033860715949],[-122.23852157592772,37.5124492009751],[-122.2418689727783,37.513810842731]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolygon")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esrispatialrelcontains")
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
				
				
				//Polygon2 Contains (Inside two polygon )  Expected : Airport, MLNH
				@Test
			    public void testPolygonContains2() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.25113868713377,37.5159894178683],[-122.24950790405273,37.514763977179],[-122.24907875061034,37.5159213383579],[-122.25053787231444,37.51694252449245],[-122.25113868713377,37.5159894178683]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolygon")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esrispatialrelcontains")
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(2))
			                .body("features.attributes.name", hasItems("MarkLogic Neighborhood","Airport"))
			                ;                                                       
					}
				
				
				
				//Polygon2 Contains ( Inside a polygon and intersecting 2 other polygons )  Expected : A, MLNH
				@Test
			    public void testPolygonContains3() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.24907875061034,37.51067902955361],[-122.24547386169434,37.51067902955361],[-122.24547386169434,37.51367467967332],[-122.24907875061034,37.51367467967332],[-122.24907875061034,37.51067902955361]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolygon")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esrispatialrelcontains")
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
				
				
				//==================================Touch===========================================
				// Touched Hvy 101(Linestring) ( from Endpoint )
				@Test
			    public void testPolygonTouches1() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.26343393325807,37.521410049523055],[-122.26358413696288,37.52209930099547],[-122.26560115814208,37.5216312914303],[-122.26503252983092,37.520746319865054],[-122.2637558,37.5206187],[-122.26343393325807,37.521410049523055]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolygon")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esrispatialreltouches")
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(1))
			                .body("features[0].attributes.name", is("Hwy 101"))
			                ;                                                       
					}					
				
				// Reverse test ( Intersecting )
				@Test
			    public void testPolygonTouches2() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.25295186042784,37.511104555362934],[-122.25335955619813,37.51095987685995],[-122.25354194641113,37.51062796629347],[-122.25318789482115,37.510381160043664],[-122.25250124931334,37.510764134909536],[-122.25295186042784,37.511104555362934]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolygon")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esrispatialreltouches")
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(0))			                ;                                                       
					}					
				
				//Not touching anything
				@Test
			    public void testPolygonTouches3() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.26343393325807,37.521410049523055],[-122.26358413696288,37.52209930099547],[-122.26560115814208,37.5216312914303],[-122.26503252983092,37.520746319865054],[-122.26404547691345,37.52091650751973],[-122.26343393325807,37.521410049523055]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolygon")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esrispatialreltouches")
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(0))
			                ;                                                       
					}
				
	//==============================overlap=========================================
				
				@Test
			    public void testPolygonOverlaps1() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.25843429565428,37.51871254735555],[-122.25431442260741,37.51871254735555],[-122.25431442260741,37.52170787501458],[-122.25843429565428,37.52170787501458],[-122.25843429565428,37.51871254735555]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolygon")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esrispatialreloverlaps")
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(1))
			                .body("features[0].attributes.name", is("MarkLogic Neighborhood"))			                ;                                                       
					}
				
				@Test
			    public void testPolygonOverlaps2() throws UnsupportedEncodingException, ParseException  {

					String path = "/marklogic/GDeltGKG/FeatureServer/{layer}/query?geometryType={geometryType}&geometry={geometry}&spatialRel={spatialRel}";
					String GeometryEncoded = URLEncoder.encode("{\"rings\":[[[-122.23766326904297,37.507410910479315],[-122.23551750183105,37.507410910479315],[-122.23551750183105,37.513810842731],[-122.23766326904297,37.513810842731],[-122.23766326904297,37.507410910479315]]],\"spatialReference\":{\"wkid\":4326}}" ,"UTF-8");
					RestAssured.urlEncodingEnabled = false;
			 		
			    	RestAssured
			            .given()
			            	.pathParam("layer", 3)
			            	.pathParam("geometryType", "esriGeometryPolygon")
			            	.pathParam("geometry",GeometryEncoded)
			                .pathParam("spatialRel","esrispatialreloverlaps")
			            .when()
			                .log().uri()
			                .get(path)
			            .then()
			                .log().ifError()
			                .statusCode(200)
			                .body("features.size()", is(1))
			                .body("features[0].attributes.name", is("Wildlife Refuge"))			                ;                                                       
					}
	}
