package io.github.Hayo87.dto;

import io.github.Hayo87.type.DiffType;

public class SessionRequestDTO {
    private DiffType type;
    private String reference;
    private String subject;

    public SessionRequestDTO() {}

    public SessionRequestDTO(DiffType type, String reference, String subject) {
        this.type = type;
        this.reference = reference;
        this.subject = subject;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public DiffType getType() {
        return type;
    }
    public void setType(DiffType type) {
        this.type = type;
    }

}
