package com.becb.api.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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


    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }


}
