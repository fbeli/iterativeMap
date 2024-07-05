package com.becb.processnewpoint.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Roteiro {

    public Roteiro(){}

    public Roteiro(String title, User user, List<RouterPoint> points, String city) {
        this.title=title;
        userOwner = user;
        this.points = points;
        this.city = city;
    }

    @Id
    private String id;

    @Column
    private String title;

    @Column
    private String city;

    @Column
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "roteiro")
    private List<RouterPoint> points;

    @Column
    private boolean publico = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "user_owner_id")
    private User userOwner;

}
