package com.inv.invitation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inv.invitation.model.DietaryType;

import com.inv.invitation.repository.DietaryTypeRepository;

@Service
public class DietaryTypeService {
	
	@Autowired
	private DietaryTypeRepository repo;
	
	public List<DietaryType> getAllDietaryType() {
		return repo.findAll();
	}
}