package com.inv.invitation.repository;

import com.inv.invitation.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface InvitationNotificationRepository extends JpaRepository<InvitationNotification, Long> {
    List<InvitationNotification> findByInvitationId(Long invitationId);
}