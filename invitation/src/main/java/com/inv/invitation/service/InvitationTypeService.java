package com.inv.invitation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inv.invitation.model.InvitationType;
import com.inv.invitation.repository.InvitationTypeRepository;

@Service
public class InvitationTypeService {
    private final InvitationTypeRepository repo;

    public InvitationTypeService(InvitationTypeRepository repo) {
        this.repo = repo;
    }

    public List<InvitationType> listAll() {
        return repo.findAll();
    }

    public InvitationType create(InvitationType t) {
        return repo.save(t);
    }

    public InvitationType update(Long id, InvitationType t) {
        return repo.findById(id).map(existing -> {
            existing.setName(t.getName());
            existing.setDescription(t.getDescription());
            return repo.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("InvitationType not found"));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public InvitationType findById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
