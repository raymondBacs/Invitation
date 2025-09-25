package com.inv.invitation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationRequest {
	private Long userId;
	private String role;
	private String message;
}
