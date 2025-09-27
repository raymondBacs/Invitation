package com.inv.invitation.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicInvitationDetailRequest {
	@NotBlank
	private Long id;
	@NotBlank
	private Long invitationId;
	@NotBlank
	private Long inviteeId;
	@NotBlank
	private String invitationCode;
	@NotBlank
    private String rsvpResponse;
	
	private Integer guestCount;
	private boolean seen;
    private String dietaryType;
    private String dietaryTypeNote;
    private String accessibility;
    private String notes;
	private LocalDateTime arrivalDate;
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
}
