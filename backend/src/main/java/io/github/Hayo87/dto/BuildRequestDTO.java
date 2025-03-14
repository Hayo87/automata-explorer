package io.github.Hayo87.dto;

public class BuildRequestDTO {
    private String action;
    private Object data; 


    public BuildRequestDTO() {}

    public BuildRequestDTO( String action, Object data) {
        this.action = action;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

