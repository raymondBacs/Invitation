package com.inv.invitation.repository;

import com.inv.invitation.model.DietaryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DietaryTypeRepository extends JpaRepository<DietaryType, Long> {
    Optional<DietaryType> findByName(String name);
}