package com.noname.project.controller;

import com.noname.project.domain.User;
import com.noname.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/profile")
    public String getProfile(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        return "profile";
    }

    @PostMapping("/user/profile")
    public String updateProfile(@AuthenticationPrincipal User user,
                                @RequestParam String password,
                                @RequestParam String email) {
        userService.updateUser(user, password, email);
        return "redirect:/user/profile";
    }

    @GetMapping("/subscribe/{user}")
    public String subscribe(@AuthenticationPrincipal User currentUser,
                            @PathVariable User user) {
        userService.subscribe(currentUser, user);
        return "redirect:/user-messages/" + user.getId();
    }

    @GetMapping("/unsubscribe/{user}")
    public String unsubscribe(@AuthenticationPrincipal User currentUser,
                            @PathVariable User user) {
        userService.unsubscribe(currentUser, user);
        return "redirect:/user-messages/" + user.getId();
    }

    @GetMapping("/user/{type}/{user}/list")
    public String userList(@PathVariable User user,
                           @PathVariable String type,
                           Model model) {
        model.addAttribute("userChannel", user);
        model.addAttribute("type", type);
        if ("subscriptions".equals(type))
            model.addAttribute("users", user.getSubscriptions());
        else
            model.addAttribute("users", user.getSubscribers());
        return "subscriptions";
    }
}
