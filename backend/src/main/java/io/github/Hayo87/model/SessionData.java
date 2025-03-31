package io.github.Hayo87.model;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

public class SessionData {
    String dotReference;
    String dotSubject;

    DiffAutomaton<String> reference;
    DiffAutomaton<String> subject;
    DiffAutomaton<String> diffAutomaton;

    // TransitionHiders
    // Synoniemen

    public DiffAutomaton<String> getDiffAutomaton() { return diffAutomaton;}
    public void setDiffAutomaton(DiffAutomaton<String> diffAutomaton) { this.diffAutomaton = diffAutomaton;}

    public DiffAutomaton<String> getReference() { return reference;}
    public void setReference(DiffAutomaton<String> reference) { this.reference = reference;}

    public DiffAutomaton<String> getSubject() { return subject;}
    public void setSubject(DiffAutomaton<String> subject) { this.subject = subject;}
}

