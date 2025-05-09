package io.github.Hayo87.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.Hayo87.domain.rules.AutomataType;
import io.github.Hayo87.dto.BuildDTO;
import io.github.Hayo87.dto.BuildResponseDTO;
import io.github.Hayo87.dto.SessionResponseDTO;
import io.github.Hayo87.services.BuildService;
import io.github.Hayo87.services.SessionService;

@WebMvcTest(ExplorerController.class)
@AutoConfigureMockMvc
@Import(ExplorerControllerTest.TestConfig.class)
public class ExplorerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestConfig{

        @Bean
        public SessionService sessionservice(){
            return mock(SessionService.class);
        }

        @Bean
        public BuildService buildservice(){
            return mock(BuildService.class);
        }
    }

    @Autowired
    private SessionService sessionservice;

    @Autowired
    private BuildService buildservice;

    private String loadJson(String name) throws IOException {
        return new String(getClass().getResourceAsStream("../resources/json/" + name).readAllBytes(), StandardCharsets.UTF_8);
    }
    
    @Test
    void createSession_whenValidInput_returns200() throws Exception {
        String json = loadJson("create-session-valid.json");

        given(sessionservice.createSession(any(), any(), any())).willReturn("ascs-d0s3d");
        doNothing().when(buildservice).buildInputs(any());
        given(buildservice.getProcessingOptions(any())).willReturn(List.of());

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value("ascs-d0s3d"));
    }

    @Test
    void createSession_whenMissingType_returns400() throws Exception {
        String json = loadJson("create-session-missing-type.json");

        given(sessionservice.createSession(any(), any(), any())).willReturn("ascs-d0s3d");
        doNothing().when(buildservice).buildInputs(any());
        given(buildservice.getProcessingOptions(any())).willReturn(List.of());

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createSession_whenIncorrectType_returns400() throws Exception {
        String json = loadJson("create-session-incorrect-type.json");

        given(sessionservice.createSession(any(), any(), any())).willReturn("ascs-d0s3d");
        doNothing().when(buildservice).buildInputs(any());
        given(buildservice.getProcessingOptions(any())).willReturn(List.of());

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createSession_whenBlankReference_returns400() throws Exception {
        String json = loadJson("create-session-blank-reference.json");

        given(sessionservice.createSession(any(), any(), any())).willReturn("ascs-d0s3d");
        doNothing().when(buildservice).buildInputs(any());
        given(buildservice.getProcessingOptions(any())).willReturn(List.of());

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createSession_whenMalformedInput_returns400() throws Exception {
        String json = loadJson("create-session-malformed-input.json");

        given(sessionservice.createSession(any(), any(), any())).willReturn("ascs-d0s3d");
        doNothing().when(buildservice).buildInputs(any());
        given(buildservice.getProcessingOptions(any())).willReturn(List.of());

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createSession_whenParsingFails_returns400() throws Exception {
        String json = loadJson("create-session-invalid-reference.json");

        given(sessionservice.createSession(any(), any(), any())).willReturn("ascs-d0s3d");
        doThrow(new BadRequestException("Invalid reference file DOT format")).when(buildservice).buildInputs(any());
        given(buildservice.getProcessingOptions(any())).willReturn(List.of());

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createSession_whenDotProcessingFails_returns500() throws Exception {
        String json = loadJson("create-session-invalid-reference.json");

        given(sessionservice.createSession(any(), any(), any())).willReturn("ascs-d0s3d");
        doThrow(new RuntimeException("Failed to start Graphviz dot process")).when(buildservice).buildInputs(any());
        given(buildservice.getProcessingOptions(any())).willReturn(List.of());

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteSession_whenValid_returns200() throws Exception {
        given(sessionservice.terminateSession("abc123"))
            .willReturn(new SessionResponseDTO("abc123", List.of()));

        mockMvc.perform(delete("/api/session/abc123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value("abc123"));    
    } 

    @Test
    void deleteSession_whenInvalid_returns400() throws Exception {

        given(sessionservice.terminateSession("invalid"))
            .willThrow(new BadRequestException("Session does not exist"));
       
        mockMvc.perform(delete("/api/session/invalid"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());    
    } 

    @Test
    void handleBuildRequest_whenValidRequest_returns200()throws Exception {
        String json = loadJson("build-valid-action.json");

        given(buildservice.buildDiff(eq("abc-123"), any()))
            .willReturn(new BuildResponseDTO(AutomataType.STRING, new BuildDTO(List.of(), List.of()), List.of()));

        mockMvc.perform(post("/api/session/abc-123/build")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.build").exists());    
    }

    @Test
    void handleBuildRequest_whenSessionNotFound_returns400()throws Exception {
        String json = loadJson("build-valid-action.json");

        given(buildservice.buildDiff(eq("invalid-id"), any()))
            .willThrow(new BadRequestException("Session does not exist"));

        mockMvc.perform(post("/api/session/invalid-id/build")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void handleBuildRequest_whenRequestIsEmpty_returns200()throws Exception {
        String json = loadJson("build-empty-request.json");

        given(buildservice.buildDiff(eq("abc-123"), any()))
            .willReturn(new BuildResponseDTO(AutomataType.STRING, new BuildDTO(List.of(), List.of()), List.of()));

        mockMvc.perform(post("/api/session/abc-123/build")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.build").exists());  
    }

    @Test
    void handleBuildRequest_whenActionDataIsInvalid_returns400()throws Exception {
        String json = loadJson("build-invalid-action.json");

        given(buildservice.buildDiff(eq("abc-123"), any()))
            .willReturn(new BuildResponseDTO(AutomataType.STRING, new BuildDTO(List.of(), List.of()), List.of()));

        mockMvc.perform(post("/api/session/abc-123/build")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists()); 
    }

    @Test
    void handleBuildRequest_whenInternalErrorOccurs_returns500()throws Exception {
        String json = loadJson("build-valid-action.json");

        given(buildservice.buildDiff(eq("abc-123"), any()))
            .willThrow(new RuntimeException("Build process failed"));   

        mockMvc.perform(post("/api/session/abc-123/build")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").exists()); 

        verify(sessionservice).terminateSession("abc-123"); 
    }
}
