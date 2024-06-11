package com.becb.processnewpoint.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
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
    @GeneratedValue(generator = "ulid-generator")
    @GenericGenerator(name = "ulid-generator", strategy = "com.becb.processnewpoint.core.UlidGenerator")
    private String id;

    @Column
    private String title;
    @Column
    private String city;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "roteiro")
    private List<RouterPoint> points;

    @Column
    private boolean publico = true;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "user_owner_id")
    private User userOwner;

}
