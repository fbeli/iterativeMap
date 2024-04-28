package com.becb.processnewpoint.dto;

import com.becb.processnewpoint.domain.Point;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PointsResponse {

    List<Point> pointList;


}
