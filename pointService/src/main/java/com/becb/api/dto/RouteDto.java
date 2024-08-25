package com.becb.api.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RouteDto {

    String title;
    String description;
    String pointId;
    String userId;
    String language;
    String place;
    String roteiroId;


    public String toString() {
        if(description != null)
            description = description.replace("\n"," ").replace("\t"," ").replace("\"", "'");
        if(description != null)
            title = title.replace("\n"," ").replace("\t"," ").replace("\"", "'");
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }
}
