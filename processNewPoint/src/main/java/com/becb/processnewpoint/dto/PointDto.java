package com.becb.processnewpoint.dto;

import com.becb.processnewpoint.domain.LanguageEnum;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.TypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PointDto {

    public PointDto(Point point) {
        this.id = point.getId();
        this.pointId = point.getPointId();
        this.title = point.getTitle();
        this.description = point.getDescription();
        this.latitude = point.getLatitude();
        this.longitude = point.getLongitude();
        this.city = point.getCity();
        this.state = point.getState();
        this.country = point.getCountry();
        this.createTime = point.getCreateTime();
        this.audio = point.getAudio();
        this.photo = point.getPhoto();
        this.aproved =  point.getAproved();
        this.type = point.getType();
        this.language = point.getLanguage();
        this.pointParent_id = point.getPointParent() != null? point.getPointParent().getPointId():null;
        this.childrenPoints = getChildrenPoints(point.getChildrenPoints());
        this.userId = point.getUser().getUserId();
    }
    private List<String> getChildrenPoints(List<Point> childrenPoints){
        if(childrenPoints==null || childrenPoints.isEmpty()){
            return null;
        }
        List<String> childrenPointsId = new ArrayList<>();
        for(Point child : childrenPoints){
            childrenPointsId.add(child.getPointId());
        }
        return childrenPointsId;
    }

    private long id;
    private String pointId;
    private String title;
    private String description;
    private String latitude;
    private String longitude;
    private String audio;
    private String type;
    private LocalDateTime createTime;
    private String aproved;
    private LanguageEnum language;
    private String pointParent_id;
    private List<String> childrenPoints;

    private String photo;
    private String state; // Region
    private String country;
    private String city;//place
private String userId;

}
