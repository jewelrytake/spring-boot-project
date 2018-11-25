package com.noname.project.service;

import com.noname.project.domain.Role;
import com.noname.project.domain.User;
import com.noname.project.repository.UserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private MailService mailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RestTemplate restTemplate;

    @Before
    public void init() {
        userService = new UserService(userRepository, mailService, passwordEncoder, restTemplate);
    }

    @Test
    public void addUser() {
        User user = new User();
        user.setEmail("any@gmail.com");
        boolean isUserCreated = userService.addUser(user);
        assertTrue(isUserCreated);
        assertNotNull(user.getActivationCode());
        assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.USER)));

        verify(userRepository, times(1)).save(user);
        verify(mailService, times(1))
                .send(eq(user.getEmail()), anyString(), anyString());

    }

    @Test
    public void addUserFailTest() {
        User user = new User();
        user.setUsername("John");
        when(userRepository.findByUsername("John")).thenReturn(new User());
//        doReturn(new User()).when(userRepository).findByUsername("John");
        boolean isUserCreated = userService.addUser(user);
        assertFalse(isUserCreated);
        verify(userRepository, times(0)).save(any(User.class));
        verify(mailService, times(0))
                .send(anyString(), anyString(), anyString());
    }

    @Test
    public void activateUser() {
        User user = new User();
        user.setActivationCode("activate");
//        doReturn(user).when(userRepository).findByActivationCode("activate");
        when(userRepository.findByActivationCode("activate")).thenReturn(user);
        boolean isUserActivated = userService.activateUser("activate");
        System.out.println(user.getActivationCode());
        assertTrue(isUserActivated);
        assertNull(user.getActivationCode());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void activateUserFailTest() {
        boolean isUserActivated = userService.activateUser("activate me");
        assertFalse(isUserActivated);
        verify(userRepository, times(0)).save(any(User.class));
    }
}