package com.becb.processnewpoint.domain;

public enum TypeEnum {
    museum("museum"),
    restaurant("restaurant"),
    gem("gem"),
    beach("beach"),
    montain("montain"),
    other("other");

    private final String value;

    TypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
