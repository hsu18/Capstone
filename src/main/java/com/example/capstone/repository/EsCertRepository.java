package com.example.capstone.repository;

import com.example.capstone.domain.EsCert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsCertRepository extends ElasticsearchRepository<EsCert,String> {
    List<EsCert> findAll();

    List<EsCert> findEsCertsByMain(String main);
    List<EsCert> findEsCertsBySub(String sub);
    List<EsCert> findEsCertByNameAndMain(String name, String main);
    List<EsCert> findEsCertByNameAndSub(String name, String sub);
    List<EsCert> findEsCertsByName(String name);
    List<EsCert> findEsCertsByTag(String tag);

    Page<EsCert> findEsCertsByMain(Pageable pageable, String main);
    Page<EsCert> findEsCertsBySub(Pageable pageable, String sub);
    Page<EsCert> findEsCertByNameAndMain(Pageable pageable, String name, String main);
    Page<EsCert> findEsCertByNameAndSub(Pageable pageable, String name, String sub);
    Page<EsCert> findEsCertsByName(Pageable pageable, String name);
    Page<EsCert> findEsCertsByTag(Pageable pageable, String tag);

    EsCert findEsCertById(String id);
}