package com.example.capstone.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Category {
    String categoryName;
    String subCategoryName;

    public Category(String categoryName, String subCategoryName){
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
    }
}