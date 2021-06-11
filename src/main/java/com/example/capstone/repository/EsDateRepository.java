package com.example.capstone.repository;

import com.example.capstone.domain.EsDate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsDateRepository extends ElasticsearchRepository<EsDate,String> {

    List<EsDate> findAll();
    List<EsDate> findEsDatesByCertid(int id);
}
