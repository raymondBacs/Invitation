package com.inv.invitation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.inv.invitation.dto.SignupRequest;
import com.inv.invitation.dto.UserRequest;
import com.inv.invitation.dto.UserUpdateRequest;
import com.inv.invitation.dto.LoginResponse;
import com.inv.invitation.model.Role;
import com.inv.invitation.model.User;
import com.inv.invitation.model.UserDetail;
import com.inv.invitation.repository.RoleRepository;
import com.inv.invitation.repository.UserDetailRepository;
import com.inv.invitation.repository.UserRepository;
import com.inv.invitation.util.JwtUtil;

import io.jsonwebtoken.Claims;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private JwtTokenService jwtTokenService;
	
	@Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserDetailRepository userDetailRepo;
    
    public Optional<User> getById(Long id) {
    	return userRepo.findById(id);
    }
	
    public ResponseEntity<?> register(SignupRequest request) {
        // Check if email already exists
        if (userRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        userRepo.save(user);

        // role is now REQUIRED from request
        Role role = roleRepo.findByName(request.getRole().toUpperCase())
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(request.getRole().toUpperCase());
                    return roleRepo.save(r);
                });

        UserDetail detail = new UserDetail();
        detail.setUser(user);
        detail.setRole(role);
        detail.setFailedAttempts(0);
        detail.setDisabled(false);
        userDetailRepo.save(detail);

        return ResponseEntity.ok("User registered successfully with role: " + role.getName());
    }
	
	public Optional<User> updateUser(Long userId, UserUpdateRequest update) {
        return userRepo.findById(userId).map(user -> {
            // Check if the new email already exists for another user
            if (!user.getEmail().equals(update.getEmail())
                && userRepo.existsByEmail(update.getEmail())) {
                throw new IllegalArgumentException("Email already in use by another user");
            }

            user.setFirstName(update.getFirstName());
            user.setLastName(update.getLastName());
            user.setEmail(update.getEmail());

            if (update.getPassword() != null && !update.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(update.getPassword()));
            }

            return userRepo.save(user);
        });
    }
	
	public ResponseEntity<?> login(String email, String password) {
		Optional<User> userOpt = userRepo.findByEmail(email);
		if(userOpt.isEmpty())
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");		
		User user = userOpt.get();
		
		Optional<UserDetail> detailOpt = userDetailRepo.findByUser(user);
		if(detailOpt.isEmpty()) 
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User detail not found");
        UserDetail detail = detailOpt.get();

	    if (detail.isDisabled()) throw new IllegalStateException("User is suspended");
	    if (detail.getLockUntil() != null && LocalDateTime.now().isBefore(detail.getLockUntil()))
	    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account is locked. Try again later.");

	    if (!passwordEncoder.matches(password, user.getPassword())) {
	        handleFailedAttempt(detail);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");
	    }

	    resetFailedAttempts(detail);
	    
	    // remove all token in jwttoken table
	    jwtTokenService.deleteAllTokensByUser(user);
	    
	    String token = jwtUtil.generateToken(user);
	    jwtTokenService.saveToken(token, user);
	    
	    LoginResponse loginResponse = new LoginResponse(
	    		user.getId(),
	    		detail.getRole().getId(),
	    		user.getEmail(),
	    		detail.getRole().getName(),
	    		token
	    );
	    
	    return ResponseEntity.ok(loginResponse);
	}
	
	public ResponseEntity<?> validateToken(String token) {
		try {
			Claims claims = jwtUtil.validateToken(token);
			
			String email = claims.getSubject();
			
			User user = userRepo.findByEmail(email)
			        .orElseThrow(() -> new IllegalArgumentException("User not found"));
			
			UserDetail detail = userDetailRepo.findByUser(user)
                    .orElseThrow(() -> new IllegalStateException("UserDetail not found for userId " + user.getId()));
			
			String roleName = detail.getRole() != null ? detail.getRole().getName() : null;
			
			return jwtTokenService.validateToken(token, user, claims, roleName);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}
        
	}
	
	public ResponseEntity<String> logout(String token) {
		String email = jwtUtil.extractEmail(token);

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
        }

        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        jwtTokenService.deleteAllTokensByUser(userOpt.get());

        return ResponseEntity.ok("Successfully logged out.");
	}

    // New helper: returns optional User from token after validation
    public Optional<User> getUserFromToken(String token) {
        try {
            Claims claims = jwtUtil.validateToken(token);
            String email = claims.getSubject();
            return userRepo.findByEmail(email);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // New helper: is the provided user an ADMIN?
    public boolean isAdmin(User user) {
        return userDetailRepo.findByUser(user)
                .map(ud -> ud.getRole() != null && "ADMIN".equalsIgnoreCase(ud.getRole().getName()))
                .orElse(false);
    }
	
	public void deleteUserTokens(User user) {
	    jwtTokenService.deleteAllTokensByUser(user);
	}

	private void handleFailedAttempt(UserDetail userDetail) {
	    userDetail.setFailedAttempts(userDetail.getFailedAttempts() + 1);
	    int attempts = userDetail.getFailedAttempts();

	    if (attempts >= 3) {
	        if (attempts < 6) userDetail.setLockUntil(LocalDateTime.now().plusMinutes(5));
	        else if (attempts < 9) userDetail.setLockUntil(LocalDateTime.now().plusMinutes(10));
	        else userDetail.setLockUntil(LocalDateTime.now().plusHours(1));
	    }
	    userDetailRepo.save(userDetail);
	}

	private void resetFailedAttempts(UserDetail userDetail) {
	    userDetail.setFailedAttempts(0);
	    userDetail.setLockUntil(null);
	    userDetailRepo.save(userDetail);
	}

    // Admin-only: list of users with the fields requested
	public List<UserRequest> listUsers() {
	    List<User> users = userRepo.findAll();
	    return users.stream()
	        .map(u -> userDetailRepo.findByUser(u).orElse(null))
	        .filter(ud -> ud != null && !ud.isDeleted()) // exclude soft-deleted
	        .map(ud -> {
	            User u = ud.getUser();
	            String roleName = ud.getRole() != null ? ud.getRole().getName() : null;
	            return new UserRequest(
	                u.getId(),
	                u.getFirstName(),
	                u.getLastName(),
	                u.getEmail(),
	                roleName,
	                ud.isDisabled()
	            );
	        })
	        .collect(Collectors.toList());
	}
	
	public ResponseEntity<?> softDeleteUser(Long userId) {
	    return userRepo.findById(userId)
	            .map(user -> {
	                UserDetail detail = userDetailRepo.findByUser(user)
	                        .orElseThrow(() -> new IllegalStateException("UserDetail not found"));
	                detail.setDeleted(true);
	                userDetailRepo.save(detail);
	                return ResponseEntity.ok("User soft-deleted successfully");
	            })
	            .orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	public Optional<UserRequest> getUserById(Long userId) {
	    return userRepo.findById(userId)
	            .flatMap(user -> {
	                // fetch detail linked to this user
	                UserDetail detail = userDetailRepo.findByUser(user)
	                        .orElseThrow(() -> new IllegalStateException("UserDetail not found for userId " + userId));

	                // skip soft-deleted users
	                if (detail.isDeleted()) {
	                    return Optional.empty();
	                }

	                String roleName = detail.getRole() != null ? detail.getRole().getName() : null;

	                UserRequest dto = new UserRequest(
	                        user.getId(),
	                        user.getFirstName(),
	                        user.getLastName(),
	                        user.getEmail(),
	                        roleName,
	                        detail.isDisabled()
	                );
	                return Optional.of(dto);
	            });
	}
}
