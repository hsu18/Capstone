package com.example.capstone.domain;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;

@ToString
@Getter
@Document(indexName = "certindex", type = "cert")
@Setting(settingPath = "/norisettings/settings.json")
@Mapping(mappingPath = "/norisettings/mappings.json")
public class EsCert {

    private @Id
    String id;

    @Field(type = FieldType.Text, name = "name")
    private String name;

    @Field(type = FieldType.Text, name = "ministry")
    private String ministry;

    @Field(type = FieldType.Text, name = "agency")
    private String agency;

    @Field(type = FieldType.Text, name = "cost")
    private String cost;

    @Field(type = FieldType.Text, name = "tag")
    private String tag;

    @Field(type = FieldType.Long, name = "views")
    private int views;

    @Field(type = FieldType.Text, name = "url")
    private String url;

    @Field(type = FieldType.Text, name = "main")
    private String main;

    @Field(type = FieldType.Text, name = "sub")
    private String sub;

    @Field(type = FieldType.Text, name = "summary")
    private String summary;

    @Field(type = FieldType.Text, name = "duty")
    private String duty;

    @Field(type = FieldType.Text, name = "career")
    private String career;

    @Field(type = FieldType.Date, name = "modified_time")
    private Date modified_time;
}