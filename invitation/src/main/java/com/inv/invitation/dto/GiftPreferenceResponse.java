package com.inv.invitation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GiftPreferenceResponse {
	private Long id;
	private Long invitationId;
	private String giftType;
	private String description;
	private String link;
	private Integer priority;
}
