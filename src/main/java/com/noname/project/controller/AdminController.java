package com.noname.project.controller;

import com.noname.project.domain.Role;
import com.noname.project.domain.User;
import com.noname.project.repository.UserRepository;
import com.noname.project.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    @GetMapping("/list")
    public String userList(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "userList";
    }

    @GetMapping("/edit/{user}")
    public String userEditForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    @PostMapping("/edit")
    public String editUser(@RequestParam String username,
                           @RequestParam Map<String, String> form,
                           @RequestParam(name = "user_id") User user) {
        adminService.editUserByAdmin(form, user, username);
        return "redirect:/user/list";
    }
}
