package io.github.Hayo87.model;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;

public class SimpleEdge {
    private final int startId;
    private final int endId;
    private final String label;
    private final DiffKind kind;

    public SimpleEdge(int startId, int endId, String label, DiffKind kind) {
        this.startId = startId;
        this.endId = endId;
        this.label = label;
        this.kind = kind;
    }

    @Override
    public String toString() {
        return "SimpleEdge{start=" + startId +
               ", end=" + endId +
               ", label='" + label + "'" +
               ", kind=" + kind +
               "}";
    }

    public int getStartId() { return startId; }
    public int getEndId() { return endId; }
    public String getLabel() { return label; }
    public DiffKind getKind() { return kind; }
}
