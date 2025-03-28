package io.github.Hayo87.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tno.gltsdiff.glts.State;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;
import com.github.tno.gltsdiff.writers.DotWriter;

/**
 * Manages all parsing and data transformation actions.
 * 
 * @author Marijn Verheul 
 */
@Service
public class ParserService {

    /**
     * Converts DOT file (String) to a Json representaton using the Graphviz CLI.
     *
     * @param dotContent The DOT file content.
     * @return JSON representation for the file 
     * @throws IOException If an error occurs.
     */
    @SuppressWarnings("CallToPrintStackTrace")
    private JsonNode convertDotStringToJson(String dotContent) throws IOException {

        
        // Create and start process
        ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tjson0");
        processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);   
        Process process = processBuilder.start();
        
        try (BufferedWriter writerPipe = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            
            // Inject dotContent as input
            writerPipe.write(dotContent);
            writerPipe.flush();
            writerPipe.close();
            
            // Read result as JsonNode
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(reader);    
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to convert dotString to Json", e);
        }
    }

    /**
     * Converts a DiffAutomaton to its DOT representation (String).
     *
     * @param automaton The automaton to convert.
     * @param writer The writer instance to use for conversion.
     * @return A string, the DOT file representation.
     */
    private <T> String convertDiffAutomatonToDotString(DiffAutomaton<T> automaton, DotWriter<?, ?, DiffAutomaton<T>> writer) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            writer.write(automaton, outputStream);

            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert automaton to dotString", e);
        }
    }
 
    /**
     * Parses a DOT string and converts it into a DiffAutomaton.
     *
     * @param dotContent The DOT file content as a string.
     * @return Parsed DiffAutomaton object.
     * @throws IOException If parsing fails.
     */
    public DiffAutomaton<String> convertDotStringToDiffAutomaton(String dotContent, Boolean reference) throws IOException {
        JsonNode graphJson = convertDotStringToJson(dotContent);
    
        // Initialize variables
        DiffAutomaton<String> automaton = new DiffAutomaton<>();
        DiffKind diffKind = reference ? DiffKind.REMOVED : DiffKind.ADDED;
        Map<String, State<DiffAutomatonStateProperty>> stateMap = new HashMap<>();
    
        // Parse nodes
        for (JsonNode node : graphJson.get("objects")) {
            String stateId = node.get("_gvid").asText();

            Boolean startState = false;
            JsonNode shapeNode = node.get("shape");
            if (shapeNode != null) {
                String shape = node.get("shape").asText();
                startState = "doublecircle".equals(shape);
            }
           
            State<DiffAutomatonStateProperty> state = automaton.addState(
                 new DiffAutomatonStateProperty(startState, diffKind, Optional.empty())
            );
            stateMap.put(stateId, state); 
        }
    
        // Parse edges
        for (JsonNode edge : graphJson.get("edges")) {
            String from = edge.get("tail").asText();
            String to = edge.get("head").asText();
            String label = edge.get("label") != null ? edge.get("label").asText() : "";
    
            automaton.addTransition(stateMap.get(from), new DiffProperty<>(label, diffKind), stateMap.get(to));
        }
    
        return automaton;
    }
    
    /**
     * Convert the differenece automaton to the TJson (Graphviz) representation
     * @param automaton
     * @param writer, the writer to be used
     * @return
     */

    public JsonNode convertJsonToDiffAutomaton(DiffAutomaton<String> automaton, DotWriter<?, ?, DiffAutomaton<String>> writer) {
        // Convert automaton to DOT format
        String dotContent = convertDiffAutomatonToDotString(automaton, writer);

        try {
            return convertDotStringToJson(dotContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert automaton to Json", e);
        }
                  
    }
}
    
