package com.becb.processnewpoint.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Roteiro {

    public Roteiro(){}
    public Roteiro(String roteiroId){
        this.id = roteiroId;
    }

    public Roteiro(String title, User user, List<RouterPoint> points, String city) {
        this.title=title;
        userOwner = user;
        this.points = points;
        this.place = city;
    }

    @Id
    private String id;

    @Column
    private String title;

    @Column
    private String place;

    @Column
    private String description;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "roteiro", cascade = CascadeType.ALL)
    private List<RouterPoint> points = new ArrayList<RouterPoint>();

    @Column
    private boolean publico = true;

    @Column
    private String language;
    @Column
    private LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_owner_id")
    private User userOwner;

    public String  toString(){
        return "Roteiro{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", city='" + place + '\'' +
                ", description='" + description + '\'' +
                ", points=" + points +
                ", publico=" + publico +
                ", language='" + language + '\'' +
                ", userOwner=" + userOwner +
                '}';
    }
}
