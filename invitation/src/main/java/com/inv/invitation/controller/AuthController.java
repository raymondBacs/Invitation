package com.inv.invitation.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inv.invitation.dto.LoginRequest;
import com.inv.invitation.dto.SignupRequest;
import com.inv.invitation.dto.UserRequest;
import com.inv.invitation.dto.UserUpdateRequest;
import com.inv.invitation.service.RoleService;
import com.inv.invitation.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@PostMapping("/user/register")
	public ResponseEntity<?> register(
	        @Valid @RequestBody SignupRequest request,
	        @RequestHeader("Authorization") String authHeader) {
	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        return ResponseEntity.badRequest().body("Invalid token format.");
	    }
	    String token = authHeader.substring(7);
	    return userService.getUserFromToken(token)
	            .map(caller -> {
	                if (!userService.isAdmin(caller)) {
	                    return ResponseEntity.status(403).body("Admin access required.");
	                }
	                return userService.register(request);
	            })
	            .orElseGet(() -> ResponseEntity.status(401).body("Invalid token."));
	}
	
	@PostMapping("/user/registerOld")
    public ResponseEntity<?> registerOld(@Valid @RequestBody SignupRequest request) {
        return userService.register(request);
    }
	
	@PutMapping("/user/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest updateRequest, @RequestHeader("Authorization") String authHeader) {
        try {
        	if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Invalid token format.");
            }
        	
        	String token = authHeader.substring(7); // Remove "Bearer "
        	
        	// validate token and ensure caller is admin
        	return userService.getUserFromToken(token)
        			.map(caller -> {
        				if (!userService.isAdmin(caller)) {
        					return ResponseEntity.status(403).body("Admin access required.");
        				}
        				return userService.updateUser(id, updateRequest)
                                .map(user -> ResponseEntity.ok("User updated successfully"))
                                .orElseGet(() -> ResponseEntity.notFound().build());
        			})
        			.orElseGet(() -> ResponseEntity.status(401).body("Invalid token."));
        	
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
	
	@DeleteMapping("/user/delete/{id}")
	public ResponseEntity<?> deleteUser(
	        @PathVariable Long id,
	        @RequestHeader("Authorization") String authHeader) {

	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        return ResponseEntity.badRequest().body("Invalid token format.");
	    }
	    String token = authHeader.substring(7);

	    return userService.getUserFromToken(token)
	            .map(caller -> {
	                if (!userService.isAdmin(caller)) {
	                    return ResponseEntity.status(403).body("Admin access required.");
	                }
	                
	                return userService.softDeleteUser(id);
	            })
	            .orElseGet(() -> ResponseEntity.status(401).body("Invalid token."));
	}
	
	@PostMapping("/user/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		String username = request.getEmail();
		String password = request.getPassword();
        return userService.login(username, password);
    }
	
	@PostMapping("/user/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(401).body("Missing or invalid Authorization header");
		}
		
		String token = authHeader.substring(7); // Remove "Bearer "
		return userService.validateToken(token);
	}
	
	@PostMapping("/user/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token format.");
        }
		
		String token = authHeader.substring(7); // Remove "Bearer "
		return userService.logout(token);
	}
	
	@GetMapping("/user/{id}")
	public ResponseEntity<?> getUserById(
	        @RequestHeader("Authorization") String authHeader,
	        @PathVariable Long id) {
	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        return ResponseEntity.badRequest().body("Invalid token format.");
	    }
	    String token = authHeader.substring(7);

	    return userService.getUserFromToken(token)
	            .map(caller -> {
	            	if (!userService.isAdmin(caller)) {
	                    return ResponseEntity.status(403).body("Admin access required.");
	                }
	            	
	            	Optional<UserRequest> userOpt = userService.getUserById(id);
	            	
	            	if (userOpt.isPresent()) {
	                    return ResponseEntity.ok(userOpt.get());
	                } else {
	                    return ResponseEntity.status(404).body("User not found or has been deleted");
	                }
	            })
	            .orElseGet(() -> ResponseEntity.status(401).body("Invalid token."));
	}
	
	@GetMapping("/users")
    public ResponseEntity<?> listUsers(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token format.");
        }
        String token = authHeader.substring(7);

        return userService.getUserFromToken(token)
                .map(caller -> {
                    if (!userService.isAdmin(caller)) {
                        return ResponseEntity.status(403).body("Admin access required.");
                    }
                    List<UserRequest> list = userService.listUsers();
                    return ResponseEntity.ok(list);
                })
                .orElseGet(() -> ResponseEntity.status(401).body("Invalid token."));
    }

	@GetMapping("/roles")
	public ResponseEntity<?> listRoles(@RequestHeader("Authorization") String authHeader) {
	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        return ResponseEntity.badRequest().body("Invalid token format.");
	    }
	    String token = authHeader.substring(7);

	    return userService.getUserFromToken(token)
	            .map(caller -> {
	                if (!userService.isAdmin(caller)) {
	                    return ResponseEntity.status(403).body("Admin access required.");
	                }
	                return ResponseEntity.ok(roleService.listRoles());
	            })
	            .orElseGet(() -> ResponseEntity.status(401).body("Invalid token."));
	}
	
	@GetMapping("/test")
    public String Testing() {
        return "Hello Test!!";
    }
	
}
