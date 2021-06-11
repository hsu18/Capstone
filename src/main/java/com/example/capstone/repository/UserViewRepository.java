package com.example.capstone.repository;

import com.example.capstone.domain.UserView;
import org.springframework.data.jpa.repository.JpaRepository;

// 사용자별 조회기록 repository(UserView)
public interface UserViewRepository extends JpaRepository<UserView, Integer> {
}
