package io.github.Hayo87.model.Handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tno.gltsdiff.builders.lts.automaton.diff.DiffAutomatonStructureComparatorBuilder;
import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomata;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;
import com.github.tno.gltsdiff.operators.hiders.SubstitutionHider;

import io.github.Hayo87.model.Filters.DiffAutomatonFilter;

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
        builder.setDiffAutomatonTransitionPropertyHider(new SubstitutionHider<>(""));
        var comparator = builder.createComparator();

        return comparator.compare(reference, subject);
    }

    @Override
    public JsonNode serialize(DiffAutomaton<String> automaton) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
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
            attributes.put("label", edge.getProperty().getProperty());
            attributes.put("diffkind", edge.getProperty().getDiffKind().toString());

            edgeNode.set("attributes", attributes);
            edgesArray.add(edgeNode);
        }

        root.set("edges", edgesArray);
        return root; 
    }
}
