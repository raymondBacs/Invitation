package com.inv.invitation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequest {
	private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean disabled;
}