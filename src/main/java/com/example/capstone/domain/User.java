package com.example.capstone.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    private String user_id;
    private String user_pw;
    private String user_name;
    private String user_tag;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="modified_time", insertable = false, nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private java.util.Date modified_time;

    public User(EsUser esUser) {
        id = Integer.parseInt(esUser.getId());
        user_id = esUser.getUser_id();
        user_name = esUser.getUser_name();
        user_pw = esUser.getUser_pw();
        user_tag = esUser.getUser_tag();
        modified_time = esUser.getModified_time();

    }
}
