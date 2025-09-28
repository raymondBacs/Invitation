package com.inv.invitation.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inv.invitation.dto.InvitationRequest;
import com.inv.invitation.model.Invitation;
import com.inv.invitation.model.InvitationType;
import com.inv.invitation.model.User;
import com.inv.invitation.repository.InvitationRepository;

@Service
public class InvitationService {
	
	@Autowired
    private final InvitationRepository repo;

    public InvitationService(InvitationRepository repo) {
        this.repo = repo;
    }

    public Invitation create(Invitation invitation) {
    	invitation.setInvitationCode(generateUniqueCode());
        return repo.save(invitation);
    }

    public Optional<Invitation> findById(Long id) {
        return repo.findById(id).filter(i -> !i.isDeleted());
    }

    public List<Invitation> listAll() {
        return repo.findAllByDeletedFalse();
    }

    public List<Invitation> listByAccount(User account) {
        return repo.findAllByAccountAndDeletedFalse(account);
    }
    
    public ResponseEntity<?> authenticate16DigitCode(String invitationCode) {
		Optional<Invitation> invitationCodeOpt = repo.findByInvitationCode(invitationCode);
		
    	if (invitationCodeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Invitation");
        }
    	return ResponseEntity.ok(invitationCodeOpt);
	}

    public Invitation update(Long id, InvitationRequest changes, User modifier, InvitationType invitationType) {
        return repo.findById(id).map(existing -> {
            existing.setInvitationType(invitationType);
            existing.setModifiedBy(modifier);
            return repo.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
    }

    public void softDelete(Long id) {
        repo.findById(id).ifPresent(i -> {
            i.setDeleted(true);
            repo.save(i);
        });
    }
    
    public String generateUniqueCode() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16).toUpperCase();
    }
}
