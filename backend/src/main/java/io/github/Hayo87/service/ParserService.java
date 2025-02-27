package io.github.Hayo87.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tno.gltsdiff.glts.State;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;
import com.github.tno.gltsdiff.writers.DotWriter;

@Service
public class ParserService {

    /**
     * Runs Graphviz CLI to convert DOT content into a plain text representation.
     *
     * @param dotContent The DOT file content.
     * @return JSON representation for the dot. 
     * @throws IOException If an error occurs.
     */
    @SuppressWarnings("CallToPrintStackTrace")
    private JsonNode processDotWithGraphviz(String dotContent) throws IOException {
        Path tempFile = Files.createTempFile("graph", ".dot");
        Files.write(tempFile, dotContent.getBytes(), StandardOpenOption.WRITE);

        ProcessBuilder builder = new ProcessBuilder("dot", "-Tdot_json", tempFile.toString());
        Process process = builder.start();

        String output;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            output = reader.lines().collect(Collectors.joining("\n"));
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Files.deleteIfExists(tempFile);

        // Convert JSON string output to JsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(output);
    }

    
    /**
     * Parses a DOT string and converts it into a DiffAutomaton.
     *
     * @param dotContent The DOT file content as a string.
     * @return Parsed DiffAutomaton object.
     * @throws IOException If parsing fails.
     */
    public DiffAutomaton<String> parseToDiffAutomaton(String dotContent, Boolean reference) throws IOException {
        JsonNode graphJson = processDotWithGraphviz(dotContent);
    
        // Initialize variables
        DiffAutomaton<String> automaton = new DiffAutomaton<>();
        DiffKind diffKind = reference ? DiffKind.REMOVED : DiffKind.ADDED;
        Map<String, State<DiffAutomatonStateProperty>> stateMap = new HashMap<>();
    
        // Parse nodes
        for (JsonNode node : graphJson.get("objects")) {
            String stateId = node.get("_gvid").asText();
            String shape = node.get("shape").asText();
            Boolean startState = "doublecircle".equals(shape);

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
    


    public Map<String, Object> convertToJson(DiffAutomaton<String> automaton, DotWriter<?, ?, DiffAutomaton<String>> writer) {
    try {
        // Convert automaton to DOT format
        String dotContent = convertToDot(automaton, writer);
        
        // Create a process
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
            JsonNode jsonNode = objectMapper.readTree(reader);
            
            // Return result
            return generalizeJson(jsonNode);
            
        }
    } catch (IOException e) {
        throw new RuntimeException("Failed to convert automaton to JSON", e);
    }
}

    /**
     * Generalizes the JSON output from Graphviz by mapping it to a structured format including graph-level information.
     *
     * @param tJson The JSON output from Graphviz as a JsonNode.
     * @return A structured Map<String, Object> representation.
     */
    public Map<String, Object> generalizeJson(JsonNode tJson) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(tJson, new TypeReference<Map<String, Object>>() {});
        }
        
    /**
     * Converts a DiffAutomaton to its DOT representation without writing to a file.
     *
     * @param automaton The automaton to convert.
     * @param writer The writer instance to use for conversion.
     * @return A string containing the DOT representation.
     */
    public <T> String convertToDot(DiffAutomaton<T> automaton, DotWriter<?, ?, DiffAutomaton<T>> writer) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            writer.write(automaton, outputStream);

            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert automaton to DOT format", e);
        }
    }
}
    
