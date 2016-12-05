package net.wouto.proxy.response.result;

import com.fasterxml.jackson.annotation.JsonInclude;

public class BasicProperty {

    private String name;
    private String value;
    @JsonInclude(JsonInclude.Include.NON_NULL) private String signature;

    public BasicProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public BasicProperty(String name, String value, String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    public BasicProperty() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
