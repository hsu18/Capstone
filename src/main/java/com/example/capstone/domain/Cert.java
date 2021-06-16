package com.example.capstone.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
//@NoArgsConstructor

@Entity
@Table(name="cert")
public class Cert {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    private String name; //이름
    private String ministry; //관련부처
    private String agency; //시행기관
    private String cost; //비용
    private String tag; //태그
    private int views; //조회수
    private String url; //크롤링url
    @Column(columnDefinition = "TEXT")
    private String summary;
    @Column(columnDefinition = "TEXT")
    private String duty;
    @Column(columnDefinition = "TEXT")
    private String career;

    @Transient
    @OneToMany(mappedBy = "cert", cascade=CascadeType.ALL)
    @JoinColumn(name="date_id")
    private List<Date> dates;

    private String main;
    private String sub;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="modified_time", insertable = false, nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private java.util.Date modified_time;

    public Cert(){
        dates = new ArrayList<>();
    }

    public Cert(EsCert esCert){
        id = Integer.parseInt(esCert.getId());
        name = esCert.getName();
        ministry = esCert.getMinistry();
        agency = esCert.getAgency();
        cost = esCert.getCost();
        tag = esCert.getTag();
        views = esCert.getViews();
        main = esCert.getMain();
        sub = esCert.getSub();
        modified_time = esCert.getModified_time();
        url = esCert.getUrl();
        summary = esCert.getSummary();
        duty = esCert.getDuty();
        career = esCert.getCareer();
    }
}