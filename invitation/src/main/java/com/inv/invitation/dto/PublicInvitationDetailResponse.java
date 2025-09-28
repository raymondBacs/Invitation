package com.inv.invitation.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.inv.invitation.model.DietaryType;
import com.inv.invitation.model.RSVPResponseType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicInvitationDetailResponse {
	
	private Long id;
	private String firstName;
	private String lastName;
	private String rsvpResponse;
	private Integer lockedDaysBeforeEvent;
	private LocalDateTime eventDate;
	
	private String inviteeCode;
	private String invitationCode;
	private Integer guestCount;
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
    private List<RSVPResponseType> rSVPResponseTypeList;
    private List<DietaryType> dietaryTypeList;
}
