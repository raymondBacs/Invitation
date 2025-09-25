package com.inv.invitation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inv.invitation.dto.InvitationRequest;
import com.inv.invitation.model.Invitation;
import com.inv.invitation.model.InvitationType;
import com.inv.invitation.model.User;
import com.inv.invitation.repository.InvitationRepository;

@Service
public class InvitationService {
    private final InvitationRepository repo;

    public InvitationService(InvitationRepository repo) {
        this.repo = repo;
    }

    public Invitation create(Invitation invitation) {
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

    public Invitation update(Long id, InvitationRequest changes, User modifier, InvitationType invitationType) {
        return repo.findById(id).map(existing -> {
            existing.setTitle(changes.getTitle());
            existing.setMessage(changes.getMessage());
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
}
