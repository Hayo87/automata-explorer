package io.github.Hayo87.services;

import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;

import io.github.Hayo87.domain.rules.AutomataType;
/**
 * Stores all session related data including the raw and processed inputs.  
 */
public class SessionData {
    private final AutomataType type; 
    private final String rawReference;
    private final String rawSubject;
    private Automaton<String> reference;
    private Automaton<String> subject;

    public SessionData(AutomataType type, String inputReference, String inputSubject) {
        this.type = type;
        this.rawReference = inputReference;
        this.rawSubject = inputSubject;
    }

    public String getRawReference() { return rawReference;}
    public String getRawSubject() { return rawSubject;}

    public Automaton<String> getReference() { return reference;}
    public void setReference(Automaton<String> reference) { this.reference = reference;}

    public Automaton<String> getSubject() { return subject;}
    public void setSubject(Automaton<String> subject) { this.subject = subject;}
    
    public AutomataType getType(){
        return type;
    }
}

