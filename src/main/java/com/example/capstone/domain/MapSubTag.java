package com.example.capstone.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="map_sub_tag")
public class MapSubTag {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name="category_name")
    String categoryName;

    @Column(name="subcategory_name")
    String subcategoryName;
    String tag;
}
