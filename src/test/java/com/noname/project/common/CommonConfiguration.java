package com.noname.project.common;

import com.noname.project.repository.UserRepository;
import com.noname.project.service.UserDetailsServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CommonConfiguration {
    @Bean
    public UserDetailsServiceImpl userDetailsService(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }
}
