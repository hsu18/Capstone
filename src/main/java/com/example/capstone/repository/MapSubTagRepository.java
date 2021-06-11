package com.example.capstone.repository;

import com.example.capstone.domain.MapSubTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapSubTagRepository extends JpaRepository<MapSubTag, Integer> {
    MapSubTag findMapSubTagBySubcategoryName(String subcategory_name);
}
