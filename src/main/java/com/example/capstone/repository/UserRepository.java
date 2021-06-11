package com.example.capstone.repository;

import com.example.capstone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 사용자 정보 repository(User)
public interface UserRepository extends JpaRepository<User, Integer> {
}
