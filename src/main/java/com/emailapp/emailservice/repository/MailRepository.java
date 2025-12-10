package com.emailapp.emailservice.repository;

import com.emailapp.emailservice.entity.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {

    List<Mail> findBySenderUserId(Long senderId);
    Mail findMailByMailId(Long mailId);
}