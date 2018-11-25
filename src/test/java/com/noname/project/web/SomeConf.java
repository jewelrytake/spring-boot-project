package com.noname.project.web;

import com.noname.project.repository.UserRepository;
import com.noname.project.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class SomeConf {
    @Autowired
    public UserDetailsServiceImpl userDetailsServiceImpl(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }
}
