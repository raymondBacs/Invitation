package com.inv.invitation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class InvitationNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Invitation invitation;

    @ManyToOne(optional = false)
    private PublicInvitationDetail publicInvitationDetail;

    private boolean seen = false;
    private String message;
}