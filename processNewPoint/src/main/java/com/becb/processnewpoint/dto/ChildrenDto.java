package com.becb.processnewpoint.dto;


import com.becb.processnewpoint.domain.Point;
import lombok.Getter;
import lombok.Setter;

@Setter
 @Getter
public class ChildrenDto {

    ChildrenDto(Point point) {
        pointId = point.getPointId();
        language = point.getLanguage().toString();
    }
    private String pointId;
    private String language;


}
