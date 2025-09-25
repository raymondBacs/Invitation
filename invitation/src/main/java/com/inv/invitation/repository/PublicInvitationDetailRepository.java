package com.inv.invitation.repository;

import com.inv.invitation.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PublicInvitationDetailRepository extends JpaRepository<PublicInvitationDetail, Long> {
    Optional<PublicInvitationDetail> findByInvitationCode(String code);
    
 // Find by invitation + invitee
    Optional<PublicInvitationDetail> findByInvitationIdAndInviteeId(Long invitationId, Long inviteeId);
}