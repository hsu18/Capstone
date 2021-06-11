package com.example.capstone.repository;

import com.example.capstone.domain.EsView;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsViewRepository extends ElasticsearchRepository<EsView,String> {
    List<EsView> findAll();

    List<EsView> findEsViewsByCertid(int certid);
    List<EsView> findEsViewsByUserid(int userid);
}