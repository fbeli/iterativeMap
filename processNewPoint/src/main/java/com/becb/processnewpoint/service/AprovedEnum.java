package com.becb.processnewpoint.service;

public enum AprovedEnum {

    asTrue("true"),
    asFalse("false"),
    asForget("forget");

    private final String value;

    AprovedEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
