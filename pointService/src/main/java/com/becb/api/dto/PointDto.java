package com.becb.api.dto;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
public class PointDto{

    private String title;
    private String lngLat;
    private String latitude;
    private String longitude;
    private String description;
    private String s3Voice;
    private String user_id;
    private String user_name;
    private String user_email;


    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }


}
