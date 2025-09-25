package com.inv.invitation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inv.invitation.model.InviteeType;

public interface InviteeTypeRepository extends JpaRepository<InviteeType, Long> {
    Optional<InviteeType> findByName(String name);
}
