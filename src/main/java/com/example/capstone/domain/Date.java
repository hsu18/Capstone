package com.example.capstone.domain;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor

@Entity
@Table(name="date")
public class Date {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    private String division; //구분
    private String date; //시험일정
    private String apply; //접수기간
    private String notif; //합격자발표기간

    @ManyToOne
    @JoinColumn(name="cert_id")
    private Cert cert;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="modified_time", insertable = false, nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private java.util.Date modified_time;

    public Date(EsDate esDate, Cert cert){
        id = Integer.parseInt(esDate.getId());
        division = esDate.getDivision();
        date = esDate.getDate();
        apply = esDate.getApply();
        notif = esDate.getNotif();
        this.cert = cert;
        modified_time = esDate.getModified_time();
    }
}