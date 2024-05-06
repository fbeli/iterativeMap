package com.becb.processnewpoint.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaceDto {

    public PlaceDto(String country, String state, String city) {
        this.country = country;
        this.state = state;
        this.city = city;
    }

    private String country;
    private String state;
    private String city;

}
