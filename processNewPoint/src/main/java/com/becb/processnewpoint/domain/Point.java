package com.becb.processnewpoint.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.becb.processnewpoint.service.SuportService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@DynamoDBTable(tableName = "points")
@Setter
@Getter
@Entity
@Table(name = "points")
public class Point {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Id
    @DynamoDBHashKey(attributeName = "pointId")
    private String pointId;

    @Column
    private String title;
    @Column
    private String latitude;
    @Column
    private String longitude;
    @Column(length = 5000)
    private String description;
    @Column
    private String shortDescription;
    @Column
    private String audio;
    @Column
    private String type;
    @Column
    private LocalDateTime createTime;

    @DynamoDBAttribute(attributeName = "aprovado")
    @Column
    private String aproved;
    @Column
    private LanguageEnum language = LanguageEnum.PT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_parent", referencedColumnName = "pointId")
    @JsonIgnore
    private Point pointParent;

    @OneToMany(mappedBy = "pointParent", fetch = FetchType.EAGER, cascade = {CascadeType.DETACH}, targetEntity = Point.class)
    private List<Point> childrenPoints;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Transient
    private List<String> photos;

    @Column
    String photo;
    @Column
    String state; // Region
    @Column
    String country;
    @Column
    String city;  //place


    public Point(String pointId) {
        this.pointId = pointId;
    }

    public Point() {
    }

    public void setDescription(String description) {

        if (description != null) {
            this.description = description;
            int size = description.length();
            if (size > 40)
                size = 40;
            this.shortDescription = description.substring(0, size);
        }
    }

    public void setLatitude(String latitude) {
        if (latitude.length() > 0)
            this.latitude = latitude.substring(latitude.indexOf(":") + 1, latitude.length() - 1).trim();
    }

    public void setLongitude(String longitude) {
        if (longitude.length() > 0)
            this.longitude = longitude.substring(longitude.indexOf(":") + 1, longitude.length() - 1).trim();
    }

    public void addChildPoint(Point point) {
        if (childrenPoints == null)
            childrenPoints = new ArrayList<>();
        childrenPoints.add(point);
    }

    public void setLanguage(String language) {
        this.language = SuportService.getLanguage(language);
    }

    public void setLanguage(LanguageEnum language) {
        this.language = language;
    }

    public void addPhoto(String photo) {
        if (photos == null)
            photos = new ArrayList<>();
        photos.add(photo);
        this.setPhoto(photo);
    }

    public String getPhotosAsString() {
        StringBuilder photosStr = new StringBuilder();
        if (photos != null) {
            photosStr.append("{");
            photos.forEach(ph ->
                photosStr.append(ph).append(",")
            );
            photosStr.append("}");
            photosStr.deleteCharAt(photosStr.length() - 2);
        }

        return photosStr.toString();
    }

    public void setType(String typestr) {
        if (typestr == null) {
            type = TypeEnum.museum.getValue();
        } else {
            if (typestr.toLowerCase().contains("museum")) {
                type = TypeEnum.museum.getValue();
            }
            if (typestr.toLowerCase().contains("gem")) {
                type = TypeEnum.gem.getValue();
            }
            if (typestr.toLowerCase().contains("rest")) {
                type = TypeEnum.restaurant.getValue();
            }
            if (typestr.toLowerCase().contains("mont")) {
                type = TypeEnum.montain.getValue();
            }
            if (typestr.toLowerCase().contains("beach")) {
                type = TypeEnum.beach.getValue();
            }
        }
    }

    public String getType() {
        if (type == null) {
            type = TypeEnum.museum.getValue();
        }
        return type;
    }

    public List<Point> getChildrenPoints(){
        if(childrenPoints == null)
            childrenPoints=new ArrayList<>();
        return childrenPoints;
    }
    public String toString() {
        return pointId + " " + title + " " + getCountry() + " " + photo;
    }
}
