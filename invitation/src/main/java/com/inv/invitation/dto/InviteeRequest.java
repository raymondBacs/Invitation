package com.inv.invitation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InviteeRequest {
	@NotBlank
	private String firstName;
	@NotBlank
	private String lastName;
	
	private String contact;
	private String email;
	private String image;
	
	@NotBlank
	private Long inviteeTypeId;
	@NotBlank
	private Long invitationId;
}