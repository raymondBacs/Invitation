package com.inv.invitation.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime modified;

    @ManyToOne
    @JoinColumn(name = "invitation_type_id")
    private InvitationType invitationType;

    // Owner account (the user who the invitation belongs to)
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private User account;

    // Who created the invitation (could be same as account)
    @ManyToOne
    @JoinColumn(name = "created_by_id", updatable = false)
    private User createdBy;

    // Who last modified the invitation
    @ManyToOne
    @JoinColumn(name = "modified_by_id")
    private User modifiedBy;
    
    @Column(unique = true, length = 16, nullable = false)
    private String invitationCode;

    private boolean deleted = false;
}
