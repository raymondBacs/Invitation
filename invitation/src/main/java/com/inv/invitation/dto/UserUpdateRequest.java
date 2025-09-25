package com.inv.invitation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
	
	@NotBlank(message = "ID is required")
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 30)
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(
        regexp = "^$|.{8,32}$",
        message = "Password must be between 8 and 32 characters if provided"
    )
    private String password; // Optional if blank (means don't update password)
}