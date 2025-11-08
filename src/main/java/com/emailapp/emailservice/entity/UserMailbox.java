package com.emailapp.emailservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_mailbox", indexes = {
        @Index(name = "idx_user_folder", columnList = "user_id, folder"),
        @Index(name = "idx_mail_id", columnList = "mail_id")
})
@Data
public class UserMailbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "mail_id", nullable = false)
    private Mail mail;

    @Enumerated(EnumType.STRING)
    @Column(name = "mail_role")
    private MailRole mailRole;

    @Enumerated(EnumType.STRING)
    private MailFolder folder = MailFolder.INBOX;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "is_starred")
    private Boolean isStarred = false;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "is_archived")
    private Boolean isArchived = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @PrePersist
    protected void onCreate() {
        receivedAt = LocalDateTime.now();
    }
}