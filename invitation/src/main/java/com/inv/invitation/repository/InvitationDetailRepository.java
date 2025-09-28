package com.inv.invitation.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inv.invitation.model.InvitationDetail;

@Repository
public interface InvitationDetailRepository extends JpaRepository<InvitationDetail, Long> {
	Optional<InvitationDetail> findByInvitationid(Long invitationId);
}