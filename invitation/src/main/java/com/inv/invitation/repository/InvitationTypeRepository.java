package com.inv.invitation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inv.invitation.model.InvitationType;

public interface InvitationTypeRepository extends JpaRepository<InvitationType, Long> {
    Optional<InvitationType> findByName(String name);
}
