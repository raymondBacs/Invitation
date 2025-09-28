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
        detail.setAuthCode(generateUniqueCode());
        detail.setSeen(false);
        return repo.save(detail);
	}
	
	public ResponseEntity<?> authenticate8DigitCode(String authCode) {    
		Optional<PublicInvitationDetail> authCodeOpt = repo.findByAuthCode(authCode);
    	
    	if (authCodeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Code");
        }
    	return ResponseEntity.ok().build();
	}
	
	public ResponseEntity<?> generatePublicInvitationURLExistingInvitee(Long inviteeId, HttpServletRequest request) {
		Optional<PublicInvitationDetail> publicInvitationDetailOpt = repo.findByInviteeId(inviteeId);
		if(publicInvitationDetailOpt.isEmpty()) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Public invitation not found.");
		}
		PublicInvitationDetail publicInvitationDetail = publicInvitationDetailOpt.get();
		
		String domain = request.getScheme() + "://" + request.getServerName() +
                (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
		
		String URL = String.format("%s/api/public-invitation/%s/%s-%s",
				domain,
				publicInvitationDetail.getInvitation().getInvitationCode(),
				publicInvitationDetail.getInvitee().getFirstName(),
				publicInvitationDetail.getInvitee().getLastName());
		
		return ResponseEntity.ok(URL);
	}
	
	public ResponseEntity<?> updateCode(Long id) {
		Optional<PublicInvitationDetail> publicInvitationDetailOpt = repo.findById(id);
		if(publicInvitationDetailOpt.isEmpty()) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Public invitation not found.");
		}
		
		PublicInvitationDetail publicInvitationDetail = publicInvitationDetailOpt.get();
		publicInvitationDetail.setAuthCode(generateUniqueCode());
		publicInvitationDetail = repo.save(publicInvitationDetail);
		
		return ResponseEntity.ok("Code Succesfully Regenerated");
	}
	
	public String generateUniqueCode() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
    }
}
