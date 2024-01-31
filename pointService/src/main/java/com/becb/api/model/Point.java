package com.becb.api.model;

import lombok.Data;

@Data
public class Point {


    private String title;
    private String latitude;
    private String longitude;

    private String description;
    private String s3Voice;
    private int starts;
    private int votes;

}
