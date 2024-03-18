package com.becb.processnewpoint.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@DynamoDBTable(tableName = "points")
@Setter
@Getter
public class Point {


    @DynamoDBHashKey(attributeName = "pointId")
    private String pointId;

    private String title;
    private String latitude;
    private String longitude;
    private String description;
    private String shortDescription;
    private String audio;

    @DynamoDBAttribute(attributeName="aprovado")
    private String aproved;
    private LanguageEnum language = LanguageEnum.PT;

    private User user;

    private List<String> photos;

    public void setDescription(String description) {
        this.description = description;
        int size = description.length();
        if(size > 40)
            size = 40;
        this.shortDescription = description.substring(0,size);
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude.substring(latitude.indexOf(":")+1,latitude.length()-1).trim();
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude.substring(longitude.indexOf(":")+1,longitude.length()-1).trim();
    }
    public void addPhoto(String photo) {
        if(photos == null)
            photos = new ArrayList<>();
        photos.add(photo);
    }
    public String getPhotosAsString(){
        StringBuilder photosStr = new StringBuilder();
        if(photos != null){
            photosStr.append("{");
            photos.forEach( photo -> {photosStr.append(photo).append(",");});
            photosStr.append("}");
            photosStr.deleteCharAt(photosStr.length()-2);
        }

        return photosStr.toString();
    }

}
