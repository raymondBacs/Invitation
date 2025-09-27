package com.inv.invitation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationFormFieldResponse {
	private boolean required;
	private boolean active;
	private String fieldName;
	private String label;
	private String fieldType;
}
