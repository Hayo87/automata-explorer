package io.github.Hayo87.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.tno.gltsdiff.glts.State;
import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

public class DiffAutomatonSerializer extends JsonSerializer<DiffAutomaton<String>> {

    @Override
    public void serialize(DiffAutomaton<String> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", "diff");


        // Serialize states
        gen.writeArrayFieldStart("nodes");
        for (State<DiffAutomatonStateProperty> state : value.getStates()) {
            gen.writeStartObject();
            gen.writeNumberField("name", state.getId());

            // Write attributes 
            gen.writeObjectFieldStart("attributes");
                gen.writeStringField("label", "S" + state.getId());
                gen.writeBooleanField("isInitial", state.getProperty().isInitial());
                gen.writeStringField("diffkind", state.getProperty().getStateDiffKind().toString());
                gen.writeEndObject();
            gen.writeEndObject();
        }
        gen.writeEndArray();

         // Serialize transitions
        Map<String, Integer> edgeCount = new HashMap<>();
        gen.writeArrayFieldStart("edges");
        for (Transition<DiffAutomatonStateProperty, DiffProperty<String>> edge : value.getTransitions()) {
            int sourceId = edge.getSource().getId();
            int targetId = edge.getTarget().getId();
            String key = sourceId + "->" + targetId;
        
            int index = edgeCount.getOrDefault(key, 0) + 1;
            edgeCount.put(key, index );
        
            String edgeId = sourceId + "-" + index + "-" + targetId;
        
            gen.writeStartObject();
            gen.writeStringField("id", edgeId);  
            gen.writeNumberField("tail", sourceId);
            gen.writeNumberField("head", targetId);
        
            // Write attributes
            gen.writeObjectFieldStart("attributes");
            gen.writeStringField("label", edge.getProperty().getProperty());
            gen.writeStringField("diffkind", edge.getProperty().getDiffKind().toString());
            gen.writeEndObject(); 
        
            gen.writeEndObject(); 
        }
        
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
