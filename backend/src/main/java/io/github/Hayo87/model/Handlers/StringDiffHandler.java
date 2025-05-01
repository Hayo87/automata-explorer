package io.github.Hayo87.model.Handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tno.gltsdiff.builders.lts.automaton.diff.DiffAutomatonStructureComparatorBuilder;
import com.github.tno.gltsdiff.glts.State;
import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomata;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.model.Filters.DiffAutomatonFilter;
import io.github.Hayo87.model.Utils.LabelUtils;

@Component
public class StringDiffHandler extends AbstractDiffHandler<String> {

    public StringDiffHandler(List<DiffAutomatonFilter<String>> filters) {
        super(filters);
    }

    @Override
    public DiffAutomaton<String> convert( Automaton<String> input, boolean isReference) {
        return DiffAutomata.fromAutomaton(input, (isReference ? DiffKind.REMOVED : DiffKind.ADDED));
    }

    @Override
    public DiffAutomaton<String> build(DiffAutomaton<String> reference, DiffAutomaton<String> subject) {
        DiffAutomatonStructureComparatorBuilder<String> builder = new DiffAutomatonStructureComparatorBuilder<>();
        //builder.setDiffAutomatonTransitionPropertyHider(new SubstitutionHider<>(""));
        builder.setRewriters(Collections.emptyList());
        var comparator = builder.createComparator();

        return comparator.compare(reference, subject);
    }

    @Override
    public JsonNode serialize(DiffAutomaton<String> automaton) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        // Serialize nodes
        ArrayNode nodesArray = mapper.createArrayNode();
        for (State<DiffAutomatonStateProperty> state : automaton.getStates()) {
            ObjectNode node = mapper.createObjectNode();
            node.put("name", state.getId());

            ObjectNode attributes = mapper.createObjectNode();
            attributes.put("label", "S" + state.getId());
            attributes.put("isInitial", state.getProperty().isInitial());
            attributes.put("diffkind", state.getProperty().getStateDiffKind().toString());

            node.set("attributes", attributes);
            nodesArray.add(node);
        }
        root.set("nodes", nodesArray);

        // Serialize edges
        ArrayNode edgesArray = mapper.createArrayNode();
        Map<String, Integer> edgeCount = new HashMap<>();

        for (Transition<DiffAutomatonStateProperty, DiffProperty<String>> edge : automaton.getTransitions()) {
            int sourceId = edge.getSource().getId();
            int targetId = edge.getTarget().getId();
            String key = sourceId + "->" + targetId;

            int index = edgeCount.getOrDefault(key, 0) + 1;
            edgeCount.put(key, index);

            String edgeId = sourceId + "-" + index + "-" + targetId;

            ObjectNode edgeNode = mapper.createObjectNode();
            edgeNode.put("id", edgeId);
            edgeNode.put("tail", sourceId);
            edgeNode.put("head", targetId);

            ObjectNode attributes = mapper.createObjectNode();
            String diffKind = edge.getProperty().getDiffKind().toString();
            attributes.put("diffkind", diffKind);

            ArrayNode labelArray = mapper.createArrayNode();

            String input = LabelUtils.extractInput(edge.getProperty().getProperty());
            String output = LabelUtils.extractOutput(edge.getProperty().getProperty());
    
            // Input
            ObjectNode inputNode = mapper.createObjectNode();
            inputNode.put("type", "input");
            inputNode.put("value", input);
            labelArray.add(inputNode);

            // Output
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("type", "output");
            outputNode.put("value", output);
            labelArray.add(outputNode);

            attributes.set("label", labelArray);
            edgeNode.set("attributes", attributes);
            edgesArray.add(edgeNode);
        }

        root.set("edges", edgesArray);
        return root; 
    }
}
