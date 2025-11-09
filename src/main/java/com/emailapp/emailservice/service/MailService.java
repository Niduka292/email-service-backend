package com.emailapp.emailservice.service;

import com.emailapp.emailservice.dto.request.SendMailRequest;
import com.emailapp.emailservice.dto.response.MailResponse;
import com.emailapp.emailservice.entity.*;
import com.emailapp.emailservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailService {

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMailboxRepository userMailboxRepository;

    @Transactional
    public Mail sendMail(Long senderId, SendMailRequest request) {
        // Get sender
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Validate recipients exist
        for (Long recipientId : request.getRecipientIds()) {
            if (!userRepository.existsById(recipientId)) {
                throw new RuntimeException("Recipient not found with id: " + recipientId);
            }
        }

        // Create mail
        Mail mail = new Mail();
        mail.setSender(sender);
        mail.setSubject(request.getSubject());
        mail.setContent(request.getContent());
        mail.setMailType(request.getMailType());

        Mail savedMail = mailRepository.save(mail);

        // Create mailbox entry for sender (SENT folder)
        UserMailbox senderMailbox = new UserMailbox();
        senderMailbox.setUser(sender);
        senderMailbox.setMail(savedMail);
        senderMailbox.setMailRole(MailRole.SENDER);
        senderMailbox.setFolder(MailFolder.SENT);
        senderMailbox.setIsRead(true);
        userMailboxRepository.save(senderMailbox);

        // Create mailbox entries for recipients (INBOX)
        for (Long recipientId : request.getRecipientIds()) {
            User recipient = userRepository.findById(recipientId)
                    .orElseThrow(() -> new RuntimeException("Recipient not found: " + recipientId));

            UserMailbox recipientMailbox = new UserMailbox();
            recipientMailbox.setUser(recipient);
            recipientMailbox.setMail(savedMail);
            recipientMailbox.setMailRole(MailRole.RECIPIENT);
            recipientMailbox.setFolder(MailFolder.INBOX);
            recipientMailbox.setIsRead(false);
            userMailboxRepository.save(recipientMailbox);
        }

        return savedMail;
    }

    public List<MailResponse> getInbox(Long userId) {
        List<UserMailbox> mailboxes = userMailboxRepository
                .findByUserUserIdAndFolderAndIsDeletedFalse(userId, MailFolder.INBOX);

        return mailboxes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<MailResponse> getSentMails(Long userId) {
        List<UserMailbox> mailboxes = userMailboxRepository
                .findByUserUserIdAndFolderAndIsDeletedFalse(userId, MailFolder.SENT);

        return mailboxes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<MailResponse> getTrash(Long userId) {
        List<UserMailbox> mailboxes = userMailboxRepository
                .findByUserUserIdAndFolderAndIsDeletedFalse(userId, MailFolder.TRASH);

        return mailboxes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMail(Long userId, Long mailId) {
        UserMailbox mailbox = userMailboxRepository
                .findByUserUserIdAndMailMailId(userId, mailId)
                .orElseThrow(() -> new RuntimeException("Mail not found in mailbox"));

        if (mailbox.getFolder() == MailFolder.TRASH) {
            // Permanent delete
            mailbox.setIsDeleted(true);
            mailbox.setDeletedAt(LocalDateTime.now());
        } else {
            // Move to trash
            mailbox.setFolder(MailFolder.TRASH);
        }

        userMailboxRepository.save(mailbox);

        // Check if all users deleted - cleanup
        cleanupMailIfNeeded(mailId);
    }

    @Transactional
    public void markAsRead(Long userId, Long mailId) {
        UserMailbox mailbox = userMailboxRepository
                .findByUserUserIdAndMailMailId(userId, mailId)
                .orElseThrow(() -> new RuntimeException("Mail not found"));

        mailbox.setIsRead(true);
        mailbox.setReadAt(LocalDateTime.now());
        userMailboxRepository.save(mailbox);
    }

    @Transactional
    public void toggleStar(Long userId, Long mailId) {
        UserMailbox mailbox = userMailboxRepository
                .findByUserUserIdAndMailMailId(userId, mailId)
                .orElseThrow(() -> new RuntimeException("Mail not found"));

        mailbox.setIsStarred(!mailbox.getIsStarred());
        userMailboxRepository.save(mailbox);
    }

    public Long getUnreadCount(Long userId) {
        return userMailboxRepository
                .findByUserUserIdAndIsReadFalseAndIsDeletedFalse(userId)
                .stream()
                .count();
    }

    private void cleanupMailIfNeeded(Long mailId) {
        List<UserMailbox> allMailboxes = userMailboxRepository.findByMailMailId(mailId);

        boolean allDeleted = allMailboxes.stream()
                .allMatch(UserMailbox::getIsDeleted);

        if (allDeleted) {
            mailRepository.deleteById(mailId);
            userMailboxRepository.deleteAll(allMailboxes);
        }
    }

    private MailResponse convertToResponse(UserMailbox mailbox) {
        Mail mail = mailbox.getMail();
        MailResponse response = new MailResponse();
        response.setMailId(mail.getMailId());
        response.setSubject(mail.getSubject());
        response.setContent(mail.getContent());
        response.setSenderName(mail.getSender().getFirstName() + " " +
                mail.getSender().getLastName());
        response.setSenderEmail(mail.getSender().getEmail());
        response.setSentAt(mail.getSentAt());
        response.setIsRead(mailbox.getIsRead());
        response.setIsStarred(mailbox.getIsStarred());
        response.setFolder(mailbox.getFolder().toString());
        return response;
    }
}