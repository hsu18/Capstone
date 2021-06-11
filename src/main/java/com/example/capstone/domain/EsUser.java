package com.example.capstone.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Getter
@Document(indexName = "userindex", type = "user")
public class EsUser {

    private @Id
    String id;

    @Field(type = FieldType.Text, name = "user_id")
    private String user_id;

    @Field(type = FieldType.Text, name = "user_name")
    private String user_name;

    @Field(type = FieldType.Text, name = "user_pw")
    private String user_pw;

    @Field(type = FieldType.Text, name = "user_tag")
    private String user_tag;

    @Field(type = FieldType.Date, name = "modified_time")
    private Date modified_time;

}