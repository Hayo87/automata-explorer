package io.github.Hayo87.dto;

public class CreateSessionRequestDTO {
    private String reference;
    private String subject;

    public CreateSessionRequestDTO() {}

    public CreateSessionRequestDTO(String reference, String subject) {
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
}
