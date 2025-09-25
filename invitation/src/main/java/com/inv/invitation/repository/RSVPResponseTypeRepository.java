package com.inv.invitation.repository;

import com.inv.invitation.model.RSVPResponseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RSVPResponseTypeRepository extends JpaRepository<RSVPResponseType, Long> {
    Optional<RSVPResponseType> findByName(String name);
}