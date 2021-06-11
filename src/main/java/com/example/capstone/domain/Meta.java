package com.example.capstone.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
//@NoArgsConstructor

@Entity
@Table(name="meta")
public class Meta {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    private String is_succeed; // 크롤링 성공여부
    private String url; // 크롤링 url

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="modified_time", insertable = false, nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date modified_time;
}
