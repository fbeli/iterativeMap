package com.becb.api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@Data
public class PointResponse {
    private String message;
    private String error;
    private String status = "200";
    private String pointId;

    public PointResponse(String message) {
        this.message = message;
    }

    public PointResponse(String status, String error) {
        this.status = status;
        this.error = error;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }
}
