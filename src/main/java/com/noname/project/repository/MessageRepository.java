package com.noname.project.repository;

import com.noname.project.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    Page<Message> findByTag(String tag, Pageable pageable);
}
