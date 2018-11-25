package com.noname.project.controller;

import com.noname.project.domain.User;
import com.noname.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping("/registration")
    public String registrationPage(Model model) {
        model.addAttribute("title", "Registration");
        model.addAttribute("form_path", "/registration");
        return "registration";
    }

    @PostMapping("/registration")
    public String registrationUser(@RequestParam("password2") String password2,
                                   @RequestParam("g-recaptcha-response") String captchaResponse,
                                   @Valid User user, BindingResult bindingResult, Model model) {
        if (userService.validUser(password2, captchaResponse, user, bindingResult, model))
            return "registration";
        if (!userService.addUser(user)) {
            model.addAttribute("message", "User already exists!");
            return "/registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable String code, Model model) {
        final boolean isActivated = userService.activateUser(code);
        if (isActivated) {
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Activation code is not found");
        }
        return "login";
    }
}
