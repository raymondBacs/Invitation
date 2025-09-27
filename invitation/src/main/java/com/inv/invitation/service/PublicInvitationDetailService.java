package com.inv.invitation.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inv.invitation.model.Invitation;
import com.inv.invitation.model.Invitee;
import com.inv.invitation.model.PublicInvitationDetail;
import com.inv.invitation.repository.PublicInvitationDetailRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PublicInvitationDetailService {
	
	@Autowired
	private PublicInvitationDetailRepository repo;
	
	public Optional<PublicInvitationDetail> getById(Long id) {
    	return repo.findById(id);
    }
	
	public PublicInvitationDetail addPublicInvitationDetailByInvitee(Invitee invitee, Invitation invitation) {
		PublicInvitationDetail detail = new PublicInvitationDetail();
		detail = new PublicInvitationDetail();
        detail.setInvitation(invitation);
        detail.setInvitee(invitee);
        detail.setInvitationCode(generateUniqueCode());
        detail.setSeen(false);
        return repo.save(detail);
	}
	
	public ResponseEntity<?> generatePublicInvitationURLExistingInvitee(Long inviteeId, HttpServletRequest request) {
		Optional<PublicInvitationDetail> publicInvitationDetailOpt = repo.findByInviteeId(inviteeId);
		
		if(!publicInvitationDetailOpt.isEmpty()) {
			PublicInvitationDetail publicInvitationDetail = publicInvitationDetailOpt.get();
			
			String domain = request.getScheme() + "://" + request.getServerName() +
	                (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
			
			String URL = String.format("%s/api/public-invitation/%s/%s-%s",
					domain,
					publicInvitationDetail.getInvitationCode(),
					publicInvitationDetail.getInvitee().getFirstName(),
					publicInvitationDetail.getInvitee().getLastName());
			
			return ResponseEntity.ok(URL);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Public invitation not found.");
		}
	}
	
	public ResponseEntity<?> generatePublicInvitationURLEmptyState(Long invitationId, HttpServletRequest request) {
		Optional<PublicInvitationDetail> publicInvitationDetailOpt = repo.findByInvitationId(invitationId);
		
		if(!publicInvitationDetailOpt.isEmpty()) {
			PublicInvitationDetail publicInvitationDetail = publicInvitationDetailOpt.get();
			
			String domain = request.getScheme() + "://" + request.getServerName() +
	                (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
			
			String URL = String.format("%s/api/public-invitation/empty-form/%s",
					domain,
					publicInvitationDetail.getInvitation().getId());
			
			return ResponseEntity.ok(URL);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Public invitation not found.");
		}
	}
	
	public String generateUniqueCode() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
    }
}
