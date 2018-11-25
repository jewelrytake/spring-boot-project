package com.noname.project.domain;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Please fill the message")
    @Length(max = 2048, message = "Message too long")
    private String text;
    @Length(max = 255, message = "Message too long (more than 255)")
    private String tag;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;
    private String filename;
}
