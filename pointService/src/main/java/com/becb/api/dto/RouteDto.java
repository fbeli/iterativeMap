package com.becb.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    String user_id;
    String language;
    String city;
    String roteiroId;


    public String toString() {
        if(description != null)
            description = description.replace("\n"," ").replace("\t"," ").replace("\"", "'");
        if(description != null)
            title = title.replace("\n"," ").replace("\t"," ").replace("\"", "'");
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }
}
