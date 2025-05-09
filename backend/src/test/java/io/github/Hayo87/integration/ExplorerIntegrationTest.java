package io.github.Hayo87.integration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ExplorerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String loadJson(String name) throws IOException {
        return new String(getClass().getResourceAsStream("../resources/json/" + name).readAllBytes(), StandardCharsets.UTF_8);
    }

    private ResultActions  startSession(String requestJson) throws Exception {
        return  mockMvc.perform(post("/api/session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson));
    }

    private ResultActions  buildSession(String sessionId, String buildJson) throws Exception {
        return  mockMvc.perform(post("/api/session/" + sessionId + "/build")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(buildJson));
    }
    
    private ResultActions  terminateSession(String sessionId) throws Exception {
        return  mockMvc.perform(delete("/api/session/" + sessionId));
    }
    
    @Test
    void startBuildTerminate_withoutActions_shouldCompleteSuccessfully() throws Exception{
        String requestJson = loadJson("create-session-valid.json");

        MvcResult startResult = startSession(requestJson)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.sessionId").exists())
                                .andExpect(jsonPath("$.processingOptions").isArray())
                                .andReturn();

        String sessionId = JsonPath.read(startResult.getResponse().getContentAsString(),"$.sessionId");  
        
        String buildJson = loadJson("build-empty-request.json");
        MvcResult buildResult = buildSession(sessionId, buildJson)
                                    .andExpect(status().isOk())
                                    .andExpect(jsonPath("$.type").exists())
                                    .andExpect(jsonPath("$.build").exists())
                                .andReturn();

        MvcResult terminateResult = terminateSession(sessionId)
                                        .andExpect(status().isOk())
                                        .andReturn();                        
    }

    @Test
    void startBuildTerminate_withValidActions_shouldCompleteSuccessfully() throws Exception{
        String requestJson = loadJson("create-session-valid.json");

        MvcResult startResult = startSession(requestJson)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.sessionId").exists())
                                .andExpect(jsonPath("$.processingOptions").isArray())
                                .andReturn();

        String sessionId = JsonPath.read(startResult.getResponse().getContentAsString(),"$.sessionId");  
        
        String buildJson = loadJson("build-valid-action.json");
        MvcResult buildResult = buildSession(sessionId, buildJson)
                                    .andExpect(status().isOk())
                                    .andExpect(jsonPath("$.type").exists())
                                    .andExpect(jsonPath("$.build").exists())
                                .andReturn();

        MvcResult terminateResult = terminateSession(sessionId)
                                        .andExpect(status().isOk())
                                        .andReturn();        
    }

    @Test
    void startBuildTerminate_withInvalidActions_shouldCompleteSuccessfully() throws Exception{
        String requestJson = loadJson("create-session-valid.json");

        MvcResult startResult = startSession(requestJson)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.sessionId").exists())
                                .andExpect(jsonPath("$.processingOptions").isArray())
                                .andReturn();

        String sessionId = JsonPath.read(startResult.getResponse().getContentAsString(),"$.sessionId");  
        
        String buildJson = loadJson("build-invalid-action.json");
        MvcResult buildResult = buildSession(sessionId, buildJson)
                                    .andExpect(status().isBadRequest())
                                    .andExpect(jsonPath("$.message").exists())
                                    .andReturn();
    }

    @Test
    void startSession_withInvalidDot_shouldFail() throws Exception {
        String requestJson = loadJson("create-session-invalid-reference.json");

        MvcResult startResult = startSession(requestJson)
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").exists())
                                .andReturn();
    }

    @Test
    void build_withoutExistingSession_shouldReturnError() throws Exception{
        String sessionId = "invalid-123";
        String buildJson = loadJson("build-valid-action.json");

        MvcResult buildResult = buildSession(sessionId, buildJson)
                                    .andExpect(status().isBadRequest())
                                    .andExpect(jsonPath("$.message").exists())
                                .andReturn();
    }

    @Test
    void terminateSession_thenReuse_shouldFail() throws Exception{
        String requestJson = loadJson("create-session-valid.json");

        MvcResult startResult = startSession(requestJson)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.sessionId").exists())
                                .andExpect(jsonPath("$.processingOptions").isArray())
                                .andReturn();

        String sessionId = JsonPath.read(startResult.getResponse().getContentAsString(),"$.sessionId");                         

        MvcResult terminateResult = terminateSession(sessionId)
                                    .andExpect(status().isOk())
                                    .andReturn();
        
        String buildJson = loadJson("build-valid-action.json");                            
        MvcResult buildResult = buildSession(sessionId, buildJson)
                                    .andExpect(status().isBadRequest())
                                    .andExpect(jsonPath("$.message").exists())
                                .andReturn();                          
    }

    @Test
    void startSession_multipleTimes_shouldIsolateSessions() throws Exception{
        String requestJson = loadJson("create-session-valid.json");

        MvcResult startResult1 = startSession(requestJson)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.sessionId").exists())
                                .andExpect(jsonPath("$.processingOptions").isArray())
                                .andReturn();

        MvcResult startResult2 = startSession(requestJson)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.sessionId").exists())
                                .andExpect(jsonPath("$.processingOptions").isArray())
                                .andReturn();

        String sessionId1 = JsonPath.read(startResult1.getResponse().getContentAsString(),"$.sessionId"); 
        String sessionId2 = JsonPath.read(startResult2.getResponse().getContentAsString(),"$.sessionId"); 

        assertNotEquals(sessionId1, sessionId2);
    }
 
}
