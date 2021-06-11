package com.example.capstone.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Document(indexName = "viewindex", type = "view_count")
public class EsView {

    private @Id
    String id;

    @Field(type = FieldType.Long, name = "userid")
    private int userid;

    @Field(type = FieldType.Long, name = "certid")
    private int certid;

    @Field(type = FieldType.Long, name = "views")
    private int views;

    @Field(type = FieldType.Date, name = "modified_time")
    private java.util.Date modified_time;

}