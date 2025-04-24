package io.github.Hayo87.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tno.gltsdiff.glts.State;
import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.AutomatonStateProperty;

import jakarta.annotation.PostConstruct;


/**
 * Manages input parsing for dotStrings  .
 * 
 * @author Marijn Verheul 
 */

@Service
public class ParserService {
    private final ObjectMapper objectMapper;

    @Autowired
    public ParserService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Parses a DOT string and converts it into a DiffAutomaton.
     *
     * @param dotContent The DOT file content as a string.
     * @return Parsed DiffAutomaton object.
     * @throws IOException If parsing fails.
     */
    public Automaton<String> convertDotStringToAutomaton(String dotContent) throws IOException {
        JsonNode graphJson = convertDotStringToJson(dotContent);
    
        // Initialize variables
        Automaton<String> automaton = new Automaton<>();
        Map<Integer, State<AutomatonStateProperty>> stateMap = new HashMap<>();
    
        // Parse nodes
        for (JsonNode node : graphJson.get("objects")) {
            int stateId = node.get("_gvid").asInt();
            Boolean startState = node.has("peripheries") && node.get("peripheries").asInt() >= 2;

            State<AutomatonStateProperty> state = automaton.addState(new AutomatonStateProperty(startState, false));
            stateMap.put(stateId, state); 
        }
    
        // Parse edges
        for (JsonNode edge : graphJson.get("edges")) {
            int from = edge.get("tail").asInt();
            int to = edge.get("head").asInt();
            String label = edge.get("label") != null ? edge.get("label").asText() : "";
    
            automaton.addTransition(stateMap.get(from), label , stateMap.get(to));
        }
        return automaton;
    }

    /**
     * Converts DOT file (String) to a Json representaton using the Graphviz CLI.
     *
     * @param dotContent The DOT file content.
     * @return JSON representation for the file 
     * @throws IOException If an error occurs.
     */
    @SuppressWarnings("CallToPrintStackTrace")
    private JsonNode convertDotStringToJson(String dotContent) throws IOException {
        long startTime = System.nanoTime();

        // Create and start process
        ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tdot_json");
        processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);   
        Process process = processBuilder.start();
        
        try (BufferedWriter writerPipe = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            
            // Inject dotContent as input
            writerPipe.write(dotContent);
            writerPipe.flush();
            writerPipe.close();

            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("Process conversion took: " + (elapsedTime / 1_000_000.0) + " ms");
            
            // Read result as JsonNode
            return objectMapper.readTree(reader);    
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to convert dotString to Json", e);
        }
    }
    
    /**
     * Method to improve the performance by warming up the process initially
     */
    @PostConstruct
    public void warmup() {
    CompletableFuture.runAsync(() -> {
        try {
            String dummy = "digraph G { a -> b }";
            Process process = new ProcessBuilder("dot", "-Tdot_json").start();
            try (var w = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                var r = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                w.write(dummy);
                w.flush();
                w.close();
                while (r.readLine() != null); 
                     process.waitFor();
                }
        } catch (IOException | InterruptedException e) {
                System.err.println("Graphviz warmup failed: " + e.getMessage());
        }
    });
    }
}
    
