package com.inv.invitation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class PublicInvitationDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Invitation invitation;

    @ManyToOne(optional = false)
    private Invitee invitee;

    @Column(unique = true, length = 8, nullable = false)
    private String invitationCode;

    private boolean seen = false;

    @ManyToOne
    private RSVPResponseType rsvpResponse;

    private Integer guestCount;

    @ManyToOne
    private DietaryType dietaryType;

    private String dietaryTypeNote;
    private String accessibility;
    private String notes;
}
