package com.example.capstone.repository;

import com.example.capstone.domain.Cert;
import com.example.capstone.domain.Date;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 자격증 세부 정보 repository(Date)
public interface DateRepository extends JpaRepository<Date, Integer> {
}
