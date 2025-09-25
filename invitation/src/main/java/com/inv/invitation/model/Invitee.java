package com.inv.invitation.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Invitee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    private String contact; // optional phone/contact
    private String email;   // optional

    // Optional image path or reference (for future dev)
    private String image;

    @ManyToOne
    @JoinColumn(name = "invitee_type_id")
    private InviteeType inviteeType;

    @ManyToOne
    @JoinColumn(name = "invitation_id", nullable = false)
    private Invitation invitation;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime modified;
    
    // Who created the invitation (could be same as account)
    @ManyToOne
    @JoinColumn(name = "created_by_id", updatable = false)
    private User createdBy;

    // Who last modified the invitation
    @ManyToOne
    @JoinColumn(name = "modified_by_id")
    private User modifiedBy;

    private boolean deleted = false;
}
