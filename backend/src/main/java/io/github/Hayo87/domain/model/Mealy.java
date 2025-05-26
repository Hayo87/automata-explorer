package io.github.Hayo87.domain.model;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.domain.rules.LabelUtils;

/**
 * Represents a Mealy transition with an input and an output.
 * 
 * <p> This record can be used a transition property in {@code DiffAutomaton<Mealy>} </p>
 * 
 * @param input the input for the transition
 * @param outputs, the outputs for the transition
 */
public class Mealy {
    DiffProperty<String> input;
    Set<DiffProperty<String>> output;

    public Mealy(DiffProperty<String> input, Set<DiffProperty<String>> output) {
        this.input = input;
        this.output = output;
    }

    public Mealy(DiffProperty<String> input, DiffProperty<String> output) {
        this(input, new LinkedHashSet<>());
        this.output.add(output);
    }

    public Mealy(String input, String output, DiffKind diffKind) {
        this(new DiffProperty<>(input, diffKind), new LinkedHashSet<>());
        this.output.add(new DiffProperty<>(output, diffKind));
    }

    public DiffProperty<String> getInput(){
        return input;
    }

    public  Set<DiffProperty<String>> getOutput() {
        return output; 
    }

    public void setInput(DiffProperty<String> newInput){
        this.input = newInput;
    }

    public void setOutput(Set<DiffProperty<String>> newOutput){
        this.output = newOutput;
    }

    public boolean isDualOutput() {
        return output.size() > 1;
    }

    /**
     * Writes a string of the form <code>input/output</code>.
     */
    @Override
    public String toString() {
        String inputString = input.getProperty();
        String outputString;

        if(!isDualOutput()) {
            outputString = output.stream()
                .findFirst()
                .map(DiffProperty:: getProperty)
                .orElse("outputString"); 
        }
        else {
            outputString = output.stream()
                .map(p -> p.getProperty() + " [" + Character.toUpperCase(p.getDiffKind().name().charAt(0))+ "]")
                .collect(Collectors.joining(" , "));
        }
        return LabelUtils.build(inputString, outputString);
    } 
}