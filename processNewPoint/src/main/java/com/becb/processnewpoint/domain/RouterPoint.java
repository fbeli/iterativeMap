package com.becb.processnewpoint.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
@Entity
@Data
public class RouterPoint {

    public RouterPoint() {}

    public RouterPoint(int position, Point point) {
        positionInRoute = position;
        this.point = point;
    }
    @Id
    @GeneratedValue(generator = "ulid-generator")
    @GenericGenerator(name = "ulid-generator", strategy = "com.becb.processnewpoint.core.UlidGenerator")
    private String id;

    @Column(name = "position_in_route")
    private int positionInRoute;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Point point;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "roteiro_id")
    private Roteiro roteiro;


}
