package com.inv.invitation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inv.invitation.model.Invitee;
import com.inv.invitation.model.Invitation;

public interface InviteeRepository extends JpaRepository<Invitee, Long> {
    List<Invitee> findAllByInvitationAndDeletedFalse(Invitation invitation);
    Optional<Invitee> findByFirstNameAndLastNameAndInvitationId(
            String firstName,
            String lastName,
            Long invitationId
    );
}
