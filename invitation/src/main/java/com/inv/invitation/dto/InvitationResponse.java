package com.inv.invitation.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.inv.invitation.model.Invitation;
import com.inv.invitation.model.InvitationDetail;
import com.inv.invitation.model.InvitationType;

@Data
public class InvitationResponse {
	// From Invitation
    private Long invitationId;
    private InvitationTypeResponse invitationType;
    private Long accountId;

    // From InvitationDetail
    private String eventTitle;
    private String introduction;
    private LocalDateTime eventDate;
    private Boolean dateLocked;
    private Integer lockedDaysBeforeEvent;
    private String dressCode;
    private String theme;
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;
    private Boolean hasGiftPreference;
    private String specialInstructions;

    /**
     * Static factory method to map Invitation + InvitationDetail to DTO
     */
    public static InvitationResponse fromEntities(
            Invitation invitation, 
            InvitationDetail detail
    ) {
        if (invitation == null || detail == null) {
            return null;
        }

        InvitationResponse dto = new InvitationResponse();
        InvitationTypeResponse invitationTypeResponse = new InvitationTypeResponse();
        
        InvitationType invitationType = invitation.getInvitationType();
        invitationTypeResponse.setId(invitationType.getId());
        invitationTypeResponse.setName(invitationType.getName());
        invitationTypeResponse.setDescription(invitationType.getDescription());
        
        // Invitation fields
        dto.setInvitationId(invitation.getId());
        dto.setInvitationType(invitationTypeResponse);

        // InvitationDetail fields
        dto.setEventTitle(detail.getEventTitle());
        dto.setIntroduction(detail.getIntroduction());
        dto.setEventDate(detail.getEvent_date());
        dto.setDateLocked(detail.getDateLocked());
        dto.setLockedDaysBeforeEvent(detail.getLockedDaysBeforeEvent());
        dto.setDressCode(detail.getDressCode());
        dto.setTheme(detail.getTheme());
        dto.setEventStartTime(detail.getEventStartTime());
        dto.setEventEndTime(detail.getEventEndTime());
        dto.setHasGiftPreference(detail.getHasGiftPreference());
        dto.setSpecialInstructions(detail.getSpecialInstructions());

        return dto;
    }
}
