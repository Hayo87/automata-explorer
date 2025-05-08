package io.github.Hayo87.services;

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

import io.github.Hayo87.controller.BadRequestException;
import jakarta.annotation.PostConstruct;

/**
 * Manages input parsing for dotStrings using the Graphviz CLI. 
 * 
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
    public Automaton<String> convertDotStringToAutomaton(String dotContent) {
        JsonNode graphJson = convertDotStringToJson(dotContent);
    
        // Initialize variables
        Automaton<String> automaton = new Automaton<>();
        Map<Integer, State<AutomatonStateProperty>> stateMap = new HashMap<>();
        int startNodeId = -1;
        int dummyId = -1;

        // Find possible start node
        for (JsonNode node : graphJson.get("objects")) {
            boolean isDummy = node.path("label").asText().isEmpty()
            && "none".equals(node.path("shape").asText());

            if(isDummy) {
                dummyId = node.path("_gvid").asInt();

                for (JsonNode edge : graphJson.get("edges")) {
                    if(dummyId == (edge.path("source").asInt())){
                        startNodeId = edge.path("target").asInt();   
                    }
                }
            }
        }    
    
        // Parse nodes
        for (JsonNode node : graphJson.get("objects")) {
            int stateId = node.get("_gvid").asInt();

            if (stateId != dummyId)  {
                Boolean startState = (stateId == startNodeId);
                State<AutomatonStateProperty> state = automaton.addState(new AutomatonStateProperty(startState, false));
                stateMap.put(stateId, state); 
            }
        }    
    
        // Parse edges
        for (JsonNode edge : graphJson.get("edges")) {
            int from = edge.get("tail").asInt();
            int to = edge.get("head").asInt();
            String label = edge.get("label") != null ? edge.get("label").asText() : "";

            if (!(from == dummyId || ((to == startNodeId) && "".equals(label)))) {
                automaton.addTransition(stateMap.get(from), label , stateMap.get(to));
            }
        }
        return automaton;
    }

    /**
     * Helper method to convert a dot string to a Json for easier parsing.
     *
     * @param dotContent The DOT string.
     * @return JSON representation for the dot string  
     * @throws IOException If an error occurs.
     */
    private JsonNode convertDotStringToJson(String dotContent) { 
        // Create and start process
        ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tdot_json");
        processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);  

        try {
            Process process = processBuilder.start();
            
            try (BufferedWriter writerPipe = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
           
            // Inject dotContent as input
            writerPipe.write(dotContent);
            writerPipe.flush();
            writerPipe.close();
            
            // Read result as JsonNode
            return objectMapper.readTree(reader);   

            }
            catch (IOException e) {
                throw new BadRequestException("Failed to convert dotString to JSon", e);
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to start Graphviz dot process", e);
        } 
    }
    
    /**
     * Method to improve the performance by warming up the Graphviz process initially
     */
    @PostConstruct
    @SuppressWarnings("empty-statement")
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
    
