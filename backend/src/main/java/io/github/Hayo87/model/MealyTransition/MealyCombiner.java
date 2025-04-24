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

        boolean combinableOutput = diffPropertyCombiner.areCombinable(lMealy.getOutput(), rMealy.getOutput());
        boolean combinableInput = diffPropertyCombiner.areCombinable(lMealy.getInput(), rMealy.getInput());

        boolean sameDiffKind =  left.getProperty() == right.getProperty();

        boolean result = ((!alreadyDual && !sameDiffKind ) && ((combinableOutput && combinableInput) || (combinableInput && !combinableOutput)));
  
        return  result;
    }
    
    @Override
    protected DiffProperty<Mealy> computeCombination(DiffProperty<Mealy> left, DiffProperty<Mealy> right) {
        Mealy lMealy = left.getProperty();
        Mealy rMealy = right.getProperty();
        Mealy nMealy; 

        System.out.println(" Combineren");
        Boolean inputCombinable = diffPropertyCombiner.areCombinable(lMealy.getInput(), rMealy.getInput());
        Boolean outputCombinable = diffPropertyCombiner.areCombinable(lMealy.getOutput(), rMealy.getOutput());

        if (inputCombinable && outputCombinable ) {
            System.out.println(" Combineren output EN input");          
            nMealy =  new Mealy( 
                        lMealy.getInput().getProperty(), 
                        lMealy.getOutput().getProperty(), 
                        DiffKind.UNCHANGED);
            return new DiffProperty<Mealy>(nMealy, DiffKind.UNCHANGED);
        }

        if (inputCombinable && !outputCombinable)  {   
            nMealy = new Mealy( 
                        new DiffProperty<>(lMealy.getInput().getProperty(), DiffKind.UNCHANGED),
                        new DiffProperty<>(lMealy.getOutput().getProperty(), lMealy.getOutput().getDiffKind()),
                        new DiffProperty<>(rMealy.getOutput().getProperty(), rMealy.getOutput().getDiffKind())); 
            return new DiffProperty<Mealy>(nMealy, DiffKind.UNCHANGED);               
        }
 
        //if(outputCombinable  && !inputCombinable){
        //    System.out.println(" Combineren output en niet input");
        //    return new Mealy(
        //            new DiffProperty<>(right.getInput().getProperty(), left.getInput().getDiffKind()),
        //            new DiffProperty<>(left.getOutput().getProperty(), DiffKind.UNCHANGED),
        //            new DiffProperty<>(right.getInput().getProperty(), right.getInput().getDiffKind()),
        //            DualKind.INPUT);  
        //}
        return left;
    }
}