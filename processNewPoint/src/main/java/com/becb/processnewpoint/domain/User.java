package com.becb.processnewpoint.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Id
    private String userId;
    @Column private String userName;
    @Column private String userEmail;
    @Column private String instagram;
    @Column private Boolean share;
    @Column private Boolean guide;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Point> points;

    public String getInstagram(){
        if (instagram == null)
            return "";
        return instagram;
    }

    public Boolean getGuide() {
        if(guide== null)
            return false;
        return guide;
    }
    public void addPoint(Point point){
        if (points == null)
            points = new HashSet<>();
        points.add(point);
    }
}
