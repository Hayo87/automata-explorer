package io.github.Hayo87.model.MealyTransition;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;
import com.github.tno.gltsdiff.operators.combiners.Combiner;
import com.github.tno.gltsdiff.operators.combiners.EqualityCombiner;
import com.github.tno.gltsdiff.operators.combiners.lts.automaton.diff.DiffKindCombiner;
import com.github.tno.gltsdiff.operators.combiners.lts.automaton.diff.DiffPropertyCombiner;

public class MealyCombiner extends Combiner<DiffProperty<Mealy>> {

    private static final DiffPropertyCombiner<String> diffPropertyCombiner = new DiffPropertyCombiner<>(new EqualityCombiner<>(), new DiffKindCombiner());
    @Override
    protected boolean computeAreCombinable(DiffProperty<Mealy> left, DiffProperty<Mealy> right) { 
        Mealy lMealy = left.getProperty();
        Mealy rMealy = right.getProperty();

        boolean alreadyDual = lMealy.isDual() || rMealy.isDual();
        boolean combinableInput = diffPropertyCombiner.areCombinable(lMealy.getInput(), rMealy.getInput());

        return (!alreadyDual && combinableInput); 
    }
    
    @Override
    protected DiffProperty<Mealy> computeCombination(DiffProperty<Mealy> left, DiffProperty<Mealy> right) {
        Mealy lMealy = left.getProperty();
        Mealy rMealy = right.getProperty();
        Mealy combinedMealy; 

        Boolean inputCombinable = diffPropertyCombiner.areCombinable(lMealy.getInput(), rMealy.getInput());
        Boolean outputCombinable = diffPropertyCombiner.areCombinable(lMealy.getOutput(), rMealy.getOutput());

        if (inputCombinable && outputCombinable ) {          
            combinedMealy =  new Mealy( 
                                lMealy.getInput().getProperty(), 
                                lMealy.getOutput().getProperty(), 
                                DiffKind.UNCHANGED);
        }
        else { // inputCombinable && !outputCombinable
            combinedMealy = new Mealy( 
                                new DiffProperty<>(lMealy.getInput().getProperty(), DiffKind.UNCHANGED),
                                new DiffProperty<>(lMealy.getOutput().getProperty(), lMealy.getOutput().getDiffKind()),
                                new DiffProperty<>(rMealy.getOutput().getProperty(), rMealy.getOutput().getDiffKind()));                
        }
        return new DiffProperty<>(combinedMealy, DiffKind.UNCHANGED);
    }
}