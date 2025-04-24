package io.github.Hayo87;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.fasterxml.jackson.databind.ObjectMapper;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicFlowTests {

    @LocalServerPort
    private int port;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Step 1: Create Session for Each Test Case
    @ParameterizedTest
    @MethodSource("loadDotFiles")
    @Order(1)
    public void testCreateSession(String referenceGraph, String subjectGraph, String type) {
        try {
            // Escape for JSON request
            String requestBody = objectMapper.writeValueAsString(new DotRequest(referenceGraph, subjectGraph, type));
    
            String sessionId =
                given()
                    .port(port)
                    .contentType(ContentType.JSON)
                    .body(requestBody)  
                    .when()
                    .post("/api/session")
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("sessionId");
    
            assertNotNull(sessionId, "Session ID should not be null");
    
            // Call build test immediately after creating session
            testGetBuildForSession(sessionId);

            // Call session deletion
            testCloseSession(sessionId);

        } catch (Exception e) {
            throw new RuntimeException("Error serializing request body", e);
        }
    }
    
    // Step 2: Get Build Using The Generated Session
    private void testGetBuildForSession(String sessionId) {
        assumeTrue(sessionId != null, "Session ID must be available");

        given()
            .port(port)
            .contentType("application/json")
            .body("{\"action\": \"build\"}")
            .when()
            .post("/api/session/{sessionId}/build", sessionId)
            .then()
            .statusCode(200) 
            .body(not(empty())); 
    }

    // Step 3: Close the session after build test
    private void testCloseSession(String sessionId) {
        assumeTrue(sessionId != null, "Session ID must be available");

        given()
            .port(port)
            .when()
            .delete("/api/session/{sessionId}", sessionId)
            .then()
            .statusCode(200) 
            .body(not(empty())); 
    }

    // Load DOT files dynamically from the directory
    private static Stream<Arguments> loadDotFiles() throws IOException {
        String folderPath = "src/test/java/io/github/Hayo87/resources/dot-files/";
        List<Path> referenceFiles = new ArrayList<>();
        List<Path> subjectFiles = new ArrayList<>();
    
        // Find and categorize files
        Files.walk(Paths.get(folderPath))
             .filter(Files::isRegularFile)
             .forEach(file -> {
                 if (file.getFileName().toString().startsWith("reference")) {
                     referenceFiles.add(file);
                 } else if (file.getFileName().toString().startsWith("subject")) {
                     subjectFiles.add(file);
                 }
             });
    
        // Sort to ensure correct pairing (reference1.dot -> subject1.dot)
        referenceFiles.sort(Path::compareTo);
        subjectFiles.sort(Path::compareTo);
    
        // Prepare test cases
        List<Arguments> testCases = new ArrayList<>();
        for (int i = 0; i < Math.min(referenceFiles.size(), subjectFiles.size()); i++) {
            String referenceGraph = Files.readString(referenceFiles.get(i)); 
            String subjectGraph = Files.readString(subjectFiles.get(i));
    
            testCases.add(Arguments.of(referenceGraph, subjectGraph, "STRING"));
            testCases.add(Arguments.of(referenceGraph, subjectGraph, "MEALY"));
        }
    
        return testCases.stream();
    }

    // Helper class to ensure correct JSON structure
    private static class DotRequest {
        public String reference;
        public String subject;
        public String type;
    
        public DotRequest(String reference, String subject, String type) {
            this.reference = reference;
            this.subject = subject;
            this.type = type;
        }
    }
}
