package com.becb.processnewpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class RoteiroResponseDto {

    private String city;
    private String title;
    private String instagram;
    private int page;
    private int requestSize;

    List<RoteiroDto> roteiros;

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

}
