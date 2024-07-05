package com.becb.processnewpoint.dto;

import com.becb.processnewpoint.domain.Point;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter

public class RouterPointDto {

    public RouterPointDto(int position, Point point) {
        positionInRoute = position;
        id = point.getPointId();
        title = point.getTitle();
        latitude = point.getLatitude();
        longitude = point.getLongitude();
        pointId = point.getPointId();
        setChildrens(point.getChildrenPoints());
    }

    private String id;
    private int positionInRoute;
    private String pointId;
    private String title;
    private String latitude;
    private String longitude;
    private List<ChildrenDto> childrens;

    private void setChildrens(List<Point> childrenPoints) {
        childrens = new ArrayList<>();
        if(childrenPoints!=null && childrenPoints.size()>0){
            childrens = childrenPoints.stream()
                          .map(ChildrenDto::new)
                    .collect(Collectors.toList());

        }else {
            childrens = new ArrayList<>();
        }
    }
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

}
