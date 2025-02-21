package io.github.Hayo87.service;


import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tno.gltsdiff.glts.State;
import com.github.tno.gltsdiff.writers.DotRenderer;
import com.github.tno.gltsdiff.writers.DotWriter;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParserService {

    /**
     * Runs Graphviz CLI to convert DOT content into a plain text representation.
     *
     * @param dotContent The DOT file content.
     * @return JSON representation for the dot. 
     * @throws IOException If an error occurs.
     */
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
            State<DiffAutomatonStateProperty> state = automaton.addState(
                 new DiffAutomatonStateProperty(false, diffKind, Optional.empty())
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
    


    public Map<String, Object> convertToJson(DiffAutomaton<String> automaton) {
        Map<String, Object> jsonAutomaton = new HashMap<>();
    
        // Extract states
        List<Map<String, Object>> states = automaton.getStates().stream().map(state -> {
            Map<String, Object> stateJson = new HashMap<>();
            stateJson.put("id", state.getId());
            stateJson.put("initial", state.getProperty().isInitial());
            stateJson.put("diffKind", state.getProperty().getStateDiffKind().toString());
            return stateJson;
        }).toList();
    
        // Extract transitions
        List<Map<String, Object>> transitions = automaton.getTransitions().stream().map(transition -> {
            Map<String, Object> transitionJson = new HashMap<>();
            transitionJson.put("from", transition.getSource().getId());
            transitionJson.put("to", transition.getTarget().getId());
            transitionJson.put("label", transition.getProperty().getProperty());
            transitionJson.put("diffKind", transition.getProperty().getDiffKind().toString());
            return transitionJson;
        }).toList();
    
        jsonAutomaton.put("transitions", transitions);
        jsonAutomaton.put("states", states);
        return jsonAutomaton;
    }  




    /**
     * Converts a DiffAutomaton to its DOT representation.
     *
     * @param automaton The automaton to convert.
     * @param writer The writer instance to use for conversion.
     * @return A string containing the DOT representation.
     * @throws IOException If writing fails.
     */
    public String convertToDot(DiffAutomaton<String> automaton, 
        DotWriter<DiffAutomatonStateProperty,DiffProperty<String>, DiffAutomaton<String>> writer) throws IOException {
        // Create a temporary DOT file
        Path tempDotFile = Files.createTempFile("automaton", ".dot");

        // Write automaton to DOT format using the given writer
        writer.write(automaton, tempDotFile);

        // Read the DOT content
        String dotContent = Files.readString(tempDotFile);

        // Cleanup
        Files.deleteIfExists(tempDotFile);

        return dotContent;
    }

        /**
     * Converts a BaseAutomaton to its SVG representation.
     *
     * @param automaton The automaton to convert.
     * @param writer The writer instance to use for conversion.
     * @return A string containing the SVG representation.
     * @throws IOException If writing or rendering fails.
     */
    public String convertToSvg(DiffAutomaton<String> automaton, 
        DotWriter<DiffAutomatonStateProperty,DiffProperty<String>, DiffAutomaton<String>> writer) throws IOException {
        // Get the DOT representation from the helper method
        String dotContent = convertToDot(automaton, writer);

        // Create a temporary DOT file
        Path tempDotFile = Files.createTempFile("automaton", ".dot");
        Files.writeString(tempDotFile, dotContent);

        // Render DOT to SVG
        Path tempSvgFile = DotRenderer.renderDot(tempDotFile);

        // Read SVG content as a string
        String svgContent = Files.readString(tempSvgFile);

        // Cleanup
        Files.deleteIfExists(tempDotFile);
        Files.deleteIfExists(tempSvgFile);

        return svgContent;
    }
}
    
