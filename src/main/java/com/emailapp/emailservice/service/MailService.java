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
        System.out.println("=== SENDING MAIL ===");
        System.out.println("Sender ID: " + senderId);
        System.out.println("Request: " + request);

        // Find sender
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + senderId));

        // Find recipient by email
        User recipient = userRepository.findByEmail(request.getRecipientEmail())
                .orElseThrow(() -> new RuntimeException("Recipient not found with email: " + request.getRecipientEmail()));

        System.out.println("Sender: " + sender.getUsername() + " (" + sender.getEmail() + ")");
        System.out.println("Recipient: " + recipient.getUsername() + " (" + recipient.getEmail() + ")");

        // Create mail
        Mail mail = new Mail();
        mail.setSubject(request.getSubject());
        mail.setContent(request.getContent());
        mail.setSender(sender);
        mail.setMailType("NORMAL");
        mail.setHasAttachments(false);
        // sentAt is set by @PrePersist

        // Save mail first
        mail = mailRepository.save(mail);
        System.out.println("Mail saved with ID: " + mail.getMailId());

        // Create UserMailbox entry for RECIPIENT (their inbox)
        UserMailbox recipientMailbox = new UserMailbox();
        recipientMailbox.setMail(mail);
        recipientMailbox.setUser(recipient);
        recipientMailbox.setMailRole(MailRole.RECIPIENT);
        recipientMailbox.setFolder(MailFolder.INBOX);
        recipientMailbox.setIsRead(false);
        recipientMailbox.setIsStarred(false);
        recipientMailbox.setIsDeleted(false);
        recipientMailbox.setIsArchived(false);
        // receivedAt is set by @PrePersist

        userMailboxRepository.save(recipientMailbox);
        System.out.println("✅ Recipient mailbox entry created (INBOX)");

        // Create UserMailbox entry for SENDER (their sent folder)
        UserMailbox senderMailbox = new UserMailbox();
        senderMailbox.setMail(mail);
        senderMailbox.setUser(sender);
        senderMailbox.setMailRole(MailRole.SENDER);
        senderMailbox.setFolder(MailFolder.SENT);
        senderMailbox.setIsRead(true);  // Sender has already "read" it
        senderMailbox.setIsStarred(false);
        senderMailbox.setIsDeleted(false);
        senderMailbox.setIsArchived(false);

        userMailboxRepository.save(senderMailbox);
        System.out.println("✅ Sender mailbox entry created (SENT)");

        System.out.println("=== MAIL SENT SUCCESSFULLY ===\n");

        return mail;
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

    public String getMailContentById(Long mailId){
        try{
            String content = mailRepository.findMailByMailId(mailId).getContent();
            if(content != null){
                return content;
            }else{
                return "Email content does not exist.";
            }
        }catch(Exception e){
            System.err.println("Error fetching mail content: "+e.getMessage());
            return "Failed to fetch mail content.";
        }
    }
}