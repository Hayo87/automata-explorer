package io.github.Hayo87.domain.model;

import com.github.tno.gltsdiff.operators.combiners.Combiner;
import com.github.tno.gltsdiff.operators.combiners.EqualityCombiner;

public class MealyCombiner extends Combiner<Mealy> {

    @Override
    protected boolean computeAreCombinable(Mealy left, Mealy right) {
        EqualityCombiner<String> combiner = new EqualityCombiner<>();
        return combiner.areCombinable(left.toString(), right.toString());
    }

    @Override
    protected Mealy computeCombination(Mealy left, Mealy right) {
        return new Mealy(left.input(), left.output());
    }
}