package com.example.capstone.repository;

import com.example.capstone.domain.Cert;
import org.springframework.data.jpa.repository.JpaRepository;

// 자격증 단일정보 repository(Cert)
public interface CertRepository extends JpaRepository<Cert, Integer> {
}
