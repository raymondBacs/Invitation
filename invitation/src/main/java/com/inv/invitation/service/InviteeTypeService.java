package com.inv.invitation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inv.invitation.model.InviteeType;
import com.inv.invitation.repository.InviteeTypeRepository;

@Service
public class InviteeTypeService {
	
	@Autowired
    private final InviteeTypeRepository repo;

    public InviteeTypeService(InviteeTypeRepository repo) {
        this.repo = repo;
    }

    public List<InviteeType> listAll() {
        return repo.findAll();
    }

    public InviteeType create(InviteeType t) {
        return repo.save(t);
    }

    public InviteeType update(Long id, InviteeType t) {
        return repo.findById(id).map(existing -> {
            existing.setName(t.getName());
            existing.setDescription(t.getDescription());
            return repo.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("InviteeType not found"));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public InviteeType findById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
