package com.inv.invitation.service;

import java.util.List;

import com.inv.invitation.model.RSVPResponseType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inv.invitation.repository.RSVPResponseTypeRepository;

@Service
public class RSVPResponseTypeService {
	
	@Autowired
	private RSVPResponseTypeRepository repo;
	
	public List<RSVPResponseType> getAllRSVPResponseType() {
		return repo.findAll();
	}
}
