package io.github.Hayo87.model;

import java.util.concurrent.locks.ReentrantLock;

import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

public class SessionData {
    String inputReference;
    String inputSubject;

    Automaton<String> reference;
    Automaton<String> subject;
    DiffAutomaton<String> diffAutomaton;

    private final ReentrantLock lock = new ReentrantLock();
    private boolean ready;

    public SessionData(String inputReference, String inputSubject) {
        this.inputReference = inputReference;
        this.inputSubject = inputSubject;
    }

    public String getInputReference() { return inputReference;}
    public String getInputSubject() { return inputSubject;}

    public DiffAutomaton<String> getDiffAutomaton() { return diffAutomaton;}
    public void setDiffAutomaton(DiffAutomaton<String> diffAutomaton) { this.diffAutomaton = diffAutomaton;}

    public Automaton<String> getReference() { return reference;}
    public void setReference(Automaton<String> reference) { this.reference = reference;}

    public Automaton<String> getSubject() { return subject;}
    public void setSubject(Automaton<String> subject) { this.subject = subject;}

    public ReentrantLock getLock() { return lock;}
    public boolean isReady() { return ready;}
    public void setReady(boolean ready) { this.ready = ready;}
}

