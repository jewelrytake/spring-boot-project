package com.noname.project.controller;

import com.noname.project.domain.Message;
import com.noname.project.domain.User;
import com.noname.project.domain.dto.CaptchaResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
public class ControllerUtils {

    public static void authorExist(Iterable<Message> messages, Model model) {
        User none = User.builder().username("<none>").active(true).build();
        for (Message message : messages) {
            if (message.getAuthor() == null) {
                message.setAuthor(none);
            }
        }
        model.addAttribute("messages", messages);
    }
    public static Map<String, String> getErrors(BindingResult bindingResult) {
        final Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage
        );
        return bindingResult.getFieldErrors().stream().collect(collector);
    }

}
