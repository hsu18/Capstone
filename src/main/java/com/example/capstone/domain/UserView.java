package com.example.capstone.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name="view_count")
public class UserView {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    private int userid;
    private int certid;
    private int views;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="modified_time", insertable = false, nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date modified_time;

    public UserView(EsView esView){
        id = Integer.parseInt(esView.getId());
        userid = esView.getUserid();
        certid = esView.getCertid();
        views = esView.getViews();
        modified_time = esView.getModified_time();
    }
}
