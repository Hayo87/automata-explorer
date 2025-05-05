package io.github.Hayo87.dto;

import io.github.Hayo87.model.AutomataType;

public class SessionRequestDTO {
    private AutomataType type;
    private String reference;
    private String subject;

    public SessionRequestDTO() {}

    public SessionRequestDTO(AutomataType type, String reference, String subject) {
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

    public AutomataType getType() {
        return type;
    }
    public void setType(AutomataType type) {
        this.type = type;
    }

}
