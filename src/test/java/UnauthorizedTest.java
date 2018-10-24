import io.restassured.RestAssured;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.junit.*;

import java.io.*;
import java.util.Properties;

import static org.hamcrest.Matchers.is;


public class UnauthorizedTest extends AbstractFeatureServiceTest {

    private static Properties testProperties = new Properties();

    @BeforeClass
    public static void setupKoop() throws IOException {
        configureKoopConnectorWithUnauthorizedUser();
        testProperties.load(new FileReader(new File("gradle-test.properties")));
    }

    @AfterClass
    public static void restoreKoop() {
        configureKoopConnectorWithAuthorizedUser();
    }

    @Test
    public void testUnauthorizedUser() {
        port = testProperties.getProperty("koopUnauthorizedPort");
        super.setupRestAssured();

        ProcessBuilder pb = new ProcessBuilder("node", "server.js");
        Process p = null;

        pb.directory(new File("build/koop"));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            p = pb.start();
            waitForNodeServerToStart(p);
            performRestAssuredTest();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
            port= testProperties.getProperty("koopPort");
            super.setupRestAssured();
        }

    }

    private void waitForNodeServerToStart(Process p) throws IOException {
        long timeout = 5000;
        long startTime = System.currentTimeMillis();
        long currentTime = startTime;
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s = null;
        while(startTime + timeout > currentTime){
            if (in.ready())
                s = in.readLine();
            if (s != null) {
                System.out.println(s);
                if (s.equals("Press control + c to exit")) {
                    break;
                }
            }
            currentTime = System.currentTimeMillis();
        }
    }

    private void performRestAssuredTest() {
        String path = "/marklogic/{service}/FeatureServer/{layer}/query?resultRecordCount={resultRecordCount}&orderByFields={orderByFields}&returnGeometry={returnGeometry}";
        RestAssured
                .given()
                .pathParam("service", "GDeltGKG")
                .pathParam("layer", 4)
                .pathParam("resultRecordCount", 5)
                .pathParam("orderByFields", "name&nbspASC")
                .pathParam("returnGeometry", true)

                .when()
                .log().uri()
                .get(path)

                .then()
                .log().ifError()
                .statusCode(500)
                .log().ifValidationFails()
                .body("error", is("401 Unauthorized"))
        ;
    }

    private static void configureKoopConnectorWithUnauthorizedUser() {
        GradleConnector gradleConnector = GradleConnector.newConnector();
        gradleConnector.forProjectDirectory(new File("."));
        ProjectConnection connection = gradleConnector.connect();
        try {
            BuildLauncher launcher = connection.newBuild().withArguments("-PenvironmentName=test");
            launcher.forTasks("configureUnauthorizedKoop");
            launcher.setStandardOutput(System.out);
            launcher.setStandardError(System.err);
            launcher.run();
        } finally {
            connection.close();
        }
    }

    private static void configureKoopConnectorWithAuthorizedUser() {
        GradleConnector gradleConnector = GradleConnector.newConnector();
        gradleConnector.forProjectDirectory(new File("."));
        ProjectConnection connection = gradleConnector.connect();
        try {
            BuildLauncher launcher = connection.newBuild().withArguments("-PenvironmentName=test");
            launcher.forTasks("configureKoop");
            launcher.setStandardOutput(System.out);
            launcher.setStandardError(System.err);
            launcher.run();
        } finally {
            connection.close();
        }
    }
}
