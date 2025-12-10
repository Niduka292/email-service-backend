package com.emailapp.emailservice.controller;

import com.emailapp.emailservice.dto.request.SendMailRequest;
import com.emailapp.emailservice.dto.response.ApiResponse;
import com.emailapp.emailservice.dto.response.MailResponse;
import com.emailapp.emailservice.entity.Mail;
import com.emailapp.emailservice.service.AIService;
import com.emailapp.emailservice.service.MailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mails")
@CrossOrigin(origins = "*")  // Allow React frontend to connect
public class MailController {

    @Autowired
    private MailService mailService;

    @Autowired
    private AIService aiService;

    // POST /api/mails/send/{senderId} - Send new email
    @PostMapping("/send/{senderId}")
    public ResponseEntity<ApiResponse> sendMail(
            @PathVariable Long senderId,
            @Valid @RequestBody SendMailRequest request) {
        try {
            Mail mail = mailService.sendMail(senderId, request);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Mail sent successfully", mail)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // GET /api/mails/inbox/{userId} - Get user's inbox
    @GetMapping("/inbox/{userId}")
    public ResponseEntity<ApiResponse> getInbox(@PathVariable Long userId) {
        try {
            List<MailResponse> mails = mailService.getInbox(userId);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Inbox retrieved successfully", mails)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // GET /api/mails/sent/{userId} - Get user's sent emails
    @GetMapping("/sent/{userId}")
    public ResponseEntity<ApiResponse> getSentMails(@PathVariable Long userId) {
        try {
            List<MailResponse> mails = mailService.getSentMails(userId);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Sent mails retrieved successfully", mails)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // GET /api/mails/trash/{userId} - Get user's trash
    @GetMapping("/trash/{userId}")
    public ResponseEntity<ApiResponse> getTrash(@PathVariable Long userId) {
        try {
            List<MailResponse> mails = mailService.getTrash(userId);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Trash retrieved successfully", mails)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // DELETE /api/mails/{mailId}/user/{userId} - Delete email
    @DeleteMapping("/{mailId}/user/{userId}")
    public ResponseEntity<ApiResponse> deleteMail(
            @PathVariable Long mailId,
            @PathVariable Long userId) {
        try {
            mailService.deleteMail(userId, mailId);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Mail deleted successfully", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // PUT /api/mails/{mailId}/user/{userId}/read - Mark as read
    @PutMapping("/{mailId}/user/{userId}/read")
    public ResponseEntity<ApiResponse> markAsRead(
            @PathVariable Long mailId,
            @PathVariable Long userId) {
        try {
            mailService.markAsRead(userId, mailId);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Mail marked as read", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // PUT /api/mails/{mailId}/user/{userId}/star - Toggle star
    @PutMapping("/{mailId}/user/{userId}/star")
    public ResponseEntity<ApiResponse> toggleStar(
            @PathVariable Long mailId,
            @PathVariable Long userId) {
        try {
            mailService.toggleStar(userId, mailId);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Star toggled successfully", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // GET /api/mails/unread/{userId} - Get unread count
    @GetMapping("/unread/{userId}")
    public ResponseEntity<ApiResponse> getUnreadCount(@PathVariable Long userId) {
        try {
            Long count = mailService.getUnreadCount(userId);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Unread count retrieved", count)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{mailId}/summary")
    public ResponseEntity<String> getEmailSummary(@PathVariable Long mailId){

        try{
            String emailContent = mailService.getMailContentById(mailId);

            if(emailContent == null || emailContent.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email content not found.");
            }

            String summary = aiService.summarizeMail(emailContent);
            return ResponseEntity.ok(summary);

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing AI summary request.");
        }
    }
}

/*
Mappings

Mapped "{[/api/users/signup],methods=[POST]}"
Mapped "{[/api/users/{userId}],methods=[GET]}"
Mapped "{[/api/mails/send/{senderId}],methods=[POST]}"
Mapped "{[/api/mails/inbox/{userId}],methods=[GET]}"
Mapped "{[/api/mails/sent/{userId}],methods=[GET]}"
Mapped "{[/api/mails/{mailId}/summary],methods=[GET]}"
 */