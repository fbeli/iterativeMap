package com.becb.api.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class PointDto{

    public PointDto(String pointId){
        this.pointId = pointId;
    }

    private String pointId;
    private String title;
    private String lngLat;
    private String latitude;
    private String longitude;
    private String description;
    private String audio;
    private String user_id;
    private String user_name;
    private String user_email;
    private Boolean share;
    private Boolean guide;
    private String instagram;
    private String language;
    private String photo;
    private String type;
    private String country;
    private String city;
    private String state;
    private String aproved;
    private String createdAt;

    private List<String> children;

    public String toString() {
        if(description != null)
            description = description.replace("\n"," ").replace("\t"," ").replace("\"", "");
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

    public String getPointIdJson(){
        return "{pointId : "+pointId+"}";
    }

}
