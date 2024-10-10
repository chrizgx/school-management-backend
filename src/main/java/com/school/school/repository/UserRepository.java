package com.school.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.school.entity.User;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    List<User> findAll();

}