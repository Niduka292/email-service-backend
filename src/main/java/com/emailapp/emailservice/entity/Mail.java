package com.emailapp.emailservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mails")
@Data
public class Mail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mailId;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String mailType = "NORMAL";

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    private Boolean hasAttachments = false;

    @OneToMany(mappedBy = "mail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserMailbox> userMailboxes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }
}