package com.becb.api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PointResponse {
    private String message;
    private String error;
    private String status = "200";

    public PointResponse(String message) {
        this.message = message;
    }

    public PointResponse(String status, String error) {
        this.status = status;
        this.error = error;
    }
}
