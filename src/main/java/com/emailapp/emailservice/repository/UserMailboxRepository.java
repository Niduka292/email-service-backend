package com.emailapp.emailservice.repository;

import com.emailapp.emailservice.entity.UserMailbox;
import com.emailapp.emailservice.entity.MailFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMailboxRepository extends JpaRepository<UserMailbox, Long> {

    List<UserMailbox> findByUserUserIdAndFolderAndIsDeletedFalse(
            Long userId,
            MailFolder folder
    );

    Optional<UserMailbox> findByUserUserIdAndMailMailId(
            Long userId,
            Long mailId
    );

    List<UserMailbox> findByMailMailId(Long mailId);

    List<UserMailbox> findByUserUserIdAndIsReadFalseAndIsDeletedFalse(Long userId);

    Long countByUserUserIdAndFolderAndIsDeletedFalse(Long userId, MailFolder folder);
}