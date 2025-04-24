package io.github.Hayo87.model.Handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tno.gltsdiff.builders.lts.automaton.diff.DiffAutomatonStructureComparatorBuilder;
import com.github.tno.gltsdiff.glts.State;
import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.AutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.model.Filters.DiffAutomatonFilter;
import io.github.Hayo87.model.MealyTransition.Mealy;
import io.github.Hayo87.model.MealyTransition.MealyCombiner;
import io.github.Hayo87.model.Utils.LabelUtils;

@Component
public class MealyDiffHandler extends  AbstractDiffHandler<Mealy> {

    public MealyDiffHandler(List<DiffAutomatonFilter<Mealy>> filters) {
        super(filters);
    }

    @Override
    public DiffAutomaton<Mealy> convert(Automaton<String> input, boolean isReference) {
        DiffAutomaton<Mealy> automaton = new DiffAutomaton<>();
        DiffKind diffKind = isReference ? DiffKind.REMOVED : DiffKind.ADDED;
        
        Map<Integer, State<DiffAutomatonStateProperty>> stateMap = new HashMap<>();
        
        // Process states
        for (State<AutomatonStateProperty> s: input.getStates()) {
            int stateId = s.getId();

            State<DiffAutomatonStateProperty> state = automaton.addState(
                new DiffAutomatonStateProperty(false, diffKind, Optional.empty())
            );
            stateMap.put(stateId, state); 
        }

        // Process transitions
        for (Transition<AutomatonStateProperty, String> t: input.getTransitions()) {
            int source = t.getSource().getId();
            int target = t.getTarget().getId();
            String label = t.getProperty();
            
            String inputLabel = LabelUtils.extractInput(label);
            String outputLabel = LabelUtils.extractOutput(label);
    
            automaton.addTransition(
                new Transition<>(
                    stateMap.get(source),
                    new DiffProperty<>(new Mealy(inputLabel, outputLabel, diffKind), diffKind),
                    stateMap.get(target)
                )
            );
        }
        return automaton; 
    }

    @Override
    public DiffAutomaton<Mealy> build(DiffAutomaton<Mealy> reference, DiffAutomaton<Mealy> subject) {
        DiffAutomatonStructureComparatorBuilder<Mealy> builder = new DiffAutomatonStructureComparatorBuilder<>();
        builder.setDiffAutomatonTransitionPropertyHider((Mealy property) -> property);
        builder.setTransitionPropertyCombiner(new MealyCombiner());
        var comparator = builder.createComparator();

        return comparator.compare(reference, subject);
    }

    @Override
    public JsonNode serialize(DiffAutomaton<Mealy> value) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        root.put("name", "diff");

        // Serialize nodes
        ArrayNode nodesArray = mapper.createArrayNode();
        for (State<DiffAutomatonStateProperty> state : value.getStates()) {
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
        Map<String, Integer> edgeCount = new HashMap<>();
        ArrayNode edgesArray = mapper.createArrayNode();

        for (Transition<DiffAutomatonStateProperty, DiffProperty<Mealy>> edge : value.getTransitions()) {
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
            attributes.put("diffkind", edge.getProperty().getDiffKind().toString());

            ArrayNode labelArray = mapper.createArrayNode();

            DiffProperty<String> input = edge.getProperty().getProperty().getInput();
            DiffProperty<String> output = edge.getProperty().getProperty().getOutput();
            DiffProperty<String> additional = edge.getProperty().getProperty().getAdditionalInput();

            // Input
            ObjectNode inputNode = mapper.createObjectNode();
            inputNode.put("type", "input");
            inputNode.put("value", input.getProperty());
            inputNode.put("diffkind", input.getDiffKind().toString());
            labelArray.add(inputNode);

            // Output
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("type", "output");
            outputNode.put("value", output.getProperty());
            outputNode.put("diffkind", output.getDiffKind().toString());
            labelArray.add(outputNode);

            // Additional input (optional)
            if (additional != null) {
                ObjectNode addNode = mapper.createObjectNode();
                addNode.put("type", "input");
                addNode.put("value", additional.getProperty());
                addNode.put("diffkind", additional.getDiffKind().toString());
                labelArray.add(addNode);
            }

            attributes.set("label", labelArray);
            edgeNode.set("attributes", attributes);
            edgesArray.add(edgeNode);
        }

        root.set("edges", edgesArray);

        return root;
    }
}
