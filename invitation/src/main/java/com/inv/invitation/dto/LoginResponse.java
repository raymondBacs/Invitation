package com.inv.invitation.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
	
	private Long userId;
	private Long roleId;
	private String email;
	private String role;
	private String token;
	
}
