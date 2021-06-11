package com.example.capstone.repository;

import com.example.capstone.domain.EsUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsUserRepository extends ElasticsearchRepository<EsUser, String> {
    List<EsUser> findAll();

    EsUser findEsUserById(String id);

}