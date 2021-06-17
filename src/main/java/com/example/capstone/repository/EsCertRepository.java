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
    List<EsCert> findEsCertsBySummary(String summary);
    List<EsCert> findEsCertsByDuty(String duty);
    List<EsCert> findEsCertsByCareer(String career);
    List<EsCert> findEsCertByNameAndMain(String name, String main);
    List<EsCert> findEsCertByNameAndSub(String name, String sub);
    List<EsCert> findEsCertsByName(String name);
    List<EsCert> findEsCertsByTag(String tag);
    List<EsCert> findEsCertsBySummaryAndMain(String summary, String main);
    List<EsCert> findEsCertsBySummaryAndSub(String summary, String sub);
    List<EsCert> findEsCertsByDutyAndMain(String duty, String main);
    List<EsCert> findEsCertsByDutyAndSub(String duty, String sub);
    List<EsCert> findEsCertsByCareerAndMain(String career, String main);
    List<EsCert> findEsCertsByCareerAndSub(String career, String sub);

    Page<EsCert> findEsCertsByMain(Pageable pageable, String main);
    Page<EsCert> findEsCertsBySub(Pageable pageable, String sub);
    Page<EsCert> findEsCertsByIdIn(Pageable pageable, List<String> ids);
    Page<EsCert> findEsCertByNameAndMain(Pageable pageable, String name, String main);
    Page<EsCert> findEsCertByNameAndSub(Pageable pageable, String name, String sub);
    Page<EsCert> findEsCertsByName(Pageable pageable, String name);
    Page<EsCert> findEsCertsByTag(Pageable pageable, String tag);

    EsCert findEsCertById(String id);
}