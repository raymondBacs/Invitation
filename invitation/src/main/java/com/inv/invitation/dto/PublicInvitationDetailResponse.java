package com.inv.invitation.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicInvitationDetailResponse {
	
	private Long invitationId;
	private Long inviteeId;
	private Integer guestCount;
	private String invitationCode;
    private String rsvpResponse;
	
    private Long id;
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
    
    private List<InvitationFormFieldResponse> invitationFormField;
}
