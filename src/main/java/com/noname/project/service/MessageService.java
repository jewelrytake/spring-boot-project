package com.noname.project.service;

import com.noname.project.domain.Message;
import com.noname.project.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @Transactional(readOnly = true)
    public Page<Message> findByTag(String tag, Pageable pageable) {
        Page<Message> messages;
        if (tag != null && !tag.isEmpty()) {
            messages = messageRepository.findByTag(tag, pageable);
        } else {
            messages = messageRepository.findAll(pageable);
        }
        return messages;
    }

    public void uploadImage(MultipartFile file, Message message) throws IOException {
        if (!Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String filename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + filename));
            message.setFilename(filename);
        }
    }
}
