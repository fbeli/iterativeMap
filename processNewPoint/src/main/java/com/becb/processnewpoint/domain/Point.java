package com.becb.processnewpoint.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private TypeEnum type;
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
        language = language.toUpperCase();
        switch (language) {
            case "ENGLISH":
            case "EN":
                this.language = LanguageEnum.EN;
                break;
            case "ESPANHOL":
            case "ESPAÑOL":
            case "SPANISH":
            case "SP":
            case "ES":
                this.language = LanguageEnum.ES;
                break;
            case "FR":
                this.language = LanguageEnum.FR;
                break;
            case "DE":
                this.language = LanguageEnum.DE;
                break;

            default:
                this.language = LanguageEnum.PT;

        }
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
            photos.forEach(photo -> {
                photosStr.append(photo).append(",");
            });
            photosStr.append("}");
            photosStr.deleteCharAt(photosStr.length() - 2);
        }

        return photosStr.toString();
    }

    public void setType(String typestr) {
        if (typestr == null) {
            type = TypeEnum.museum;
        } else {
            if (typestr.toLowerCase().contains("museum")) {
                type = TypeEnum.museum;
            }
            if (typestr.toLowerCase().contains("gem")) {
                type = TypeEnum.gem;
            }
            if (typestr.toLowerCase().contains("rest")) {
                type = TypeEnum.restaurant;
            }
        }
    }

    public TypeEnum getType() {
        if (type == null) {
            type = TypeEnum.museum;
        }
        return type;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        if (photos != null && photos.size() > 0) {
            photo = photos.get(0);
        }
        return photo;
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
