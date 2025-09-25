package com.inv.invitation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvitationRequest {
	private String title;
	private String message;
	private Long invitationTypeId;
	private Long accountId;
}
