package com.inv.invitation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class InvitationForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private Invitee invitee;
}
