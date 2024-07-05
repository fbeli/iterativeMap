package com.becb.api.model;

import lombok.Data;

import java.util.List;

@Data
public class Point {


    private String title;
    private String latitude;
    private String longitude;
    private String pointId;
    private List<Point> childrenPoints;
    private String language;

    private String description;
    private String s3Voice;
    private int starts;
    private int votes;

}
