package com.noname.project.service;

import com.noname.project.controller.ControllerUtils;
import com.noname.project.domain.Role;
import com.noname.project.domain.User;
import com.noname.project.domain.dto.CaptchaResponseDto;
import com.noname.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    private final UserRepository userRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    @Value("${recaptcha.secret}")
    private String secret;

    @Value("${hostname}")
    private String hostname;

    public boolean addUser(User user) {
        final User existUser = userRepository.findByUsername(user.getUsername());
        if (existUser != null) {
            return false;
        }
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        sendMessage(user);
        return true;
    }

    private void sendMessage(User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format("Hello, %s! \n" +
                            "Welcome to project! \n" +
                            "Please, activate your account: http://%s/activate/%s",
                    user.getUsername(), hostname, user.getActivationCode());
            mailService.send(user.getEmail(), "Activation code", message);
        }
    }

    public void updateUser(User user, String password, String email) {
        final String userEmail = user.getEmail();
        if (email != null && !email.equals(userEmail) ||
                userEmail != null && !userEmail.equals(email) ||
                !StringUtils.isEmpty(password)) {
            if (!StringUtils.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
                user.setEmail(email);
            } if (!StringUtils.isEmpty(password)) {
                user.setPassword(passwordEncoder.encode(password));
            }
            userRepository.save(user);
//            new Thread(() -> sendMessage(user)).start();
        }
    }
    public boolean validUser(@RequestParam("password2") String password2, @RequestParam("g-recaptcha-response") String captchaResponse, @Valid User user, BindingResult bindingResult, Model model) {
        final String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        final CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);
        final boolean isConfirmEmpty = org.thymeleaf.util.StringUtils.isEmpty(password2);
        final boolean isPasswordDifferent = user.getPassword() != null && !user.getPassword().equals(password2);
        if (!response.isSuccess()) {
            model.addAttribute("captchaError", "Fill captcha");
        }
        if (isConfirmEmpty) {
            model.addAttribute("password2Error", "Password confirmation cannot be empty");
        }
        if(isPasswordDifferent){
            model.addAttribute("passwordError", "Passwords are different!");
        }
        if (isConfirmEmpty || bindingResult.hasErrors() || !response.isSuccess() || isPasswordDifferent) {
            model.mergeAttributes(ControllerUtils.getErrors(bindingResult));
            return true;
        }
        return false;
    }
    public boolean activateUser(String code) {
        final User user = userRepository.findByActivationCode(code);
        if (user == null) {
            return false;
        }
        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);
        return true;
    }
    public void subscribe(User currentUser, User user) {
        user.getSubscribers().add(currentUser);
        userRepository.save(user);
    }

    public void unsubscribe(User currentUser, User user) {
        user.getSubscribers().remove(currentUser);
        userRepository.save(user);
    }
}
