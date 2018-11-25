package com.noname.project.controller;

import com.noname.project.domain.Message;
import com.noname.project.domain.User;
import com.noname.project.repository.MessageRepository;
import com.noname.project.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MessageRepository messageRepository;
    private final MessageService messageService;

    @GetMapping("/")
    public String greeting(Model model) {
        model.addAttribute("title", "Welcome");
        return "greeting";
    }

    @GetMapping("/main")
    public String getMainPage(@RequestParam(name = "tag", required = false) String tag,
                              @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
                              Model model) {
        final Page<Message> messages = messageRepository.findAll(pageable);
        ControllerUtils.authorExist(messages, model);
        final Page<Message> searchByTag = messageService.findByTag(tag, pageable);
        ControllerUtils.authorExist(searchByTag, model);
        model.addAttribute("title", "Main page");
        model.addAttribute("page", messages);
        model.addAttribute("url", "/main");
        model.addAttribute("tag", tag);
        return "main";

    }

    @PostMapping("/main")
    public String addMessage(@Valid Message message,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal User user,
                             Model model,
                             @RequestParam(name = "file") MultipartFile file) throws IOException {
        message.setAuthor(user);
        if (bindingResult.hasErrors()) {
            final Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        } else {
            messageService.uploadImage(file, message);
            messageRepository.save(message);
            model.addAttribute("message", null);
        }
        model.addAttribute("messages", messageRepository.findAll());
        return "redirect:/main";
    }


    @GetMapping("/user-messages/{user}")
    public String userMessages(@AuthenticationPrincipal User currentUser,
                               @PathVariable User user,
                               Model model,
                               @RequestParam(required = false) Message message) {
        Set<Message> messages = user.getMessages();
        model.addAttribute("userChannel", user);
        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        System.out.println(user.getSubscribers().size());
        System.out.println(user.getSubscriptions().size());
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
        model.addAttribute("messages", messages);
        model.addAttribute("message", message);
        model.addAttribute("isCurrentUser", currentUser.equals(user));
        return "userMessages";
    }
    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long user,
            @RequestParam("id") Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (message.getAuthor().equals(currentUser)) {
            if (!StringUtils.isEmpty(text)) {
                message.setText(text);
            }
            if (!StringUtils.isEmpty(tag)) {
                message.setTag(tag);
            }
            messageService.uploadImage(file, message);
            messageRepository.save(message);
        }
        return "redirect:/user-messages/" + user;
    }
}

