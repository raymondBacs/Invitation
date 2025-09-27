package com.inv.invitation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.inv.invitation.model.InvitationFormField;

public interface InvitationFormFieldRepository extends JpaRepository<InvitationFormField, Long> {
    Optional<InvitationFormField> findByInvitationTypeNameAndFieldName(String invitationTypeName, String fieldName);
    
    List<InvitationFormField> findByInvitationTypeId(Long invitationTypeId);
}
