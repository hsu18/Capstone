package com.example.capstone.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Getter
@Document(indexName = "dateindex", type = "date")
public class EsDate {

    private @Id
    String id;

    @Field(type = FieldType.Text, name = "division")
    private String division;

    @Field(type = FieldType.Text, name = "date")
    private String date;

    @Field(type = FieldType.Text, name = "apply")
    private String apply;

    @Field(type = FieldType.Text, name = "notif")
    private String notif;

    @Field(type = FieldType.Long, name = "cert_id")
    private int certid;

    @Field(type = FieldType.Date, name = "modified_time")
    private Date modified_time;

//    @Override
//    public String toString() {
//        return "Book{" +
//                "id='" + id + '\'' +
//                ", name='" + name + '\'' +
//                ", ministry='" + ministry + '\'' +
//                ", agency='" + agency + '\'' +
//                ", cost='" + cost + '\'' +
//                ", tag='" + tag + '\'' +
//                ", views='" + views + '\'' +
//                '}';
//    }
}