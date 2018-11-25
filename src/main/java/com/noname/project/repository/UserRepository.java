package com.noname.project.repository;

import com.noname.project.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByActivationCode(String code);
}
