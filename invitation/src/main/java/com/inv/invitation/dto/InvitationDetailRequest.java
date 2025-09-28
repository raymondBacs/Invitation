package com.inv.invitation.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationDetailRequest {
    private Long invitationId;

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
}
