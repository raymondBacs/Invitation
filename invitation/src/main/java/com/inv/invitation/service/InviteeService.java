package com.inv.invitation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inv.invitation.model.Invitee;
import com.inv.invitation.model.Invitation;
import com.inv.invitation.repository.InviteeRepository;

@Service
public class InviteeService {
	
	@Autowired
    private final InviteeRepository repo;

    public InviteeService(InviteeRepository repo) {
        this.repo = repo;
    }

    public Invitee create(Invitee i) {
        return repo.save(i);
    }

    public List<Invitee> listByInvitation(Invitation invitation) {
        return repo.findAllByInvitationAndDeletedFalse(invitation);
    }

    public Optional<Invitee> findById(Long id) {
        return repo.findById(id).filter(i -> !i.isDeleted());
    }

    public Invitee update(Long id, Invitee changes) {
        return repo.findById(id).map(existing -> {
            existing.setFirstName(changes.getFirstName());
            existing.setLastName(changes.getLastName());
            existing.setContact(changes.getContact());
            existing.setEmail(changes.getEmail());
            existing.setImage(changes.getImage());
            existing.setInviteeType(changes.getInviteeType());
            return repo.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Invitee not found"));
    }

    public void softDelete(Long id) {
        repo.findById(id).ifPresent(i -> {
            i.setDeleted(true);
            repo.save(i);
        });
    }
}
