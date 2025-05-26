package io.github.Hayo87.domain.model;

import java.util.Set;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;
import com.github.tno.gltsdiff.operators.combiners.Combiner;
import com.github.tno.gltsdiff.operators.combiners.EqualityCombiner;
import com.github.tno.gltsdiff.operators.combiners.SetCombiner;
import com.github.tno.gltsdiff.operators.combiners.lts.automaton.diff.DiffKindCombiner;
import com.github.tno.gltsdiff.operators.combiners.lts.automaton.diff.DiffPropertyCombiner;

public class MealyCombiner extends Combiner<Mealy> {
    private final Combiner<DiffProperty<String>> inputCombiner = new DiffPropertyCombiner<>(new EqualityCombiner<>(), new DiffKindCombiner());
    private final Combiner<Set<DiffProperty<String>>> outputCombiner = new SetCombiner<>(inputCombiner);

    @Override
    protected boolean computeAreCombinable(Mealy left, Mealy right) {
        return  inputCombiner.areCombinable(left.getInput(), right.getInput()) &&
                outputCombiner.areCombinable(left.getOutput(), right.getOutput());
    }

    @Override
    protected Mealy computeCombination(Mealy left, Mealy right) {
        return new Mealy(
                inputCombiner.combine(left.getInput(), right.getInput()), 
                outputCombiner.combine(left.getOutput(), right.getOutput())
                );
    }
}