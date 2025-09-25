package com.inv.invitation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inv.invitation.model.Invitation;
import com.inv.invitation.model.User;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findAllByAccountAndDeletedFalse(User account);
    List<Invitation> findAllByDeletedFalse();
}
