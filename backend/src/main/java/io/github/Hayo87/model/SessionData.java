package io.github.Hayo87.model;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

public class SessionData {
    String inputReference;
    String inputSubject;

    DiffAutomaton<String> reference;
    DiffAutomaton<String> subject;
    DiffAutomaton<String> diffAutomaton;

    public SessionData(String inputReference, String inputSubject) {
        this.inputReference = inputReference;
        this.inputSubject = inputSubject;
    }

    public String getInputReference() { return inputReference;}
    public String getInputSubject() { return inputSubject;}

    public DiffAutomaton<String> getDiffAutomaton() { return diffAutomaton;}
    public void setDiffAutomaton(DiffAutomaton<String> diffAutomaton) { this.diffAutomaton = diffAutomaton;}

    public DiffAutomaton<String> getReference() { return reference;}
    public void setReference(DiffAutomaton<String> reference) { this.reference = reference;}

    public DiffAutomaton<String> getSubject() { return subject;}
    public void setSubject(DiffAutomaton<String> subject) { this.subject = subject;}



}

