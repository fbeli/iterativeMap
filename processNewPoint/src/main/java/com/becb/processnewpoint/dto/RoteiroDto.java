package com.becb.processnewpoint.dto;

import com.becb.processnewpoint.domain.Roteiro;
import com.becb.processnewpoint.domain.RouterPoint;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Setter
@Getter
public class RoteiroDto {

    private String title;
    private String city;
    private List<RouterPointDto> points;
    private boolean publico = true;
    private String instagram;
    private String description;
    private String roteiroId;

    public RoteiroDto(Roteiro roteiro) {
        this.title = roteiro.getTitle();
        this.city = roteiro.getPlace();
        this.instagram = roteiro.getUserOwner().getInstagram();
        this.publico = roteiro.isPublico();
        this.description = roteiro.getDescription();
        setPoints(roteiro.getPoints());
        this.roteiroId = roteiro.getId();

    }

    public void setPoints(List<RouterPoint> routePoints) {
        if( routePoints != null && !routePoints.isEmpty() ) {
            points = routePoints.stream()
                                .map(routep -> new RouterPointDto(routep.getPositionInRoute(), routep.getPoint()))
                               .sorted((o1, o2) -> o1.getPositionInRoute() - o2.getPositionInRoute())
                                .collect(Collectors.toList());
        }else {
            points = new ArrayList<>();
        }
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

}
