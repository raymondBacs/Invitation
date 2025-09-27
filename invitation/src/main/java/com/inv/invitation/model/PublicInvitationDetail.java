package com.inv.invitation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
public class PublicInvitationDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Invitation invitation;
    
    @OneToOne
    private Invitee invitee;

    @Column(unique = true, length = 8, nullable = false)
    private String invitationCode;

    private boolean seen = false;

    @ManyToOne
    private RSVPResponseType rsvpResponse;

    private Integer guestCount;
	
	private LocalDateTime arrivalDate;

    @ManyToOne
    private DietaryType dietaryType;
	
    private String dietaryTypeNote;
	
	private String email;
	private String contact;
	private String organization;
	private String sessionSelection;
	private String transportation;
	private String specialRequests;
	private String vipSeating;
	private String role;
	private String performanceRole;
	private String availability;
	private String mediaOutlet;
	private String pressId;
	private String entourageRole;
	private String company;
	private String designation;
    private String accessibility;
    private String notes;
}