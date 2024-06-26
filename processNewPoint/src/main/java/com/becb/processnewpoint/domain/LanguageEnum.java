package com.becb.processnewpoint.domain;

public enum LanguageEnum {

    PT("PT"),
    EN("EN"),
    ES("ES"),
    FR("FR"),
    DE("DE");

    private final String value;

    LanguageEnum(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }


}
