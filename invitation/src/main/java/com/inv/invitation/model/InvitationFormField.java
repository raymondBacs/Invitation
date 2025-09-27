package com.inv.invitation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class InvitationFormField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invitation_type_id", nullable = false)
    private InvitationType invitationType;

    private String fieldName;   // e.g., "dietaryPreference", "organization", "guestCount"
    private String label;       // e.g., "Dietary Preference"
    private boolean required;
    private String fieldType;   // TEXT, SELECT, NUMBER, DATE, BOOLEAN

    private boolean active = true;
}