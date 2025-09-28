package com.inv.invitation.service;

import com.inv.invitation.model.Role;
import com.inv.invitation.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
	
	@Autowired
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }
}