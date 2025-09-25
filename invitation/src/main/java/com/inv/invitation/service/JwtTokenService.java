package com.inv.invitation.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inv.invitation.config.JwtProperties;
import com.inv.invitation.dto.ValidationRequest;
import com.inv.invitation.model.JwtToken;
import com.inv.invitation.model.User;
import com.inv.invitation.repository.JwtTokenRepository;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
	
	@Autowired
	private JwtTokenRepository jwtTokenRepository;
	
	@Autowired
	private JwtProperties jwtProperties;
	
	public void saveToken(String token, User user) {
	    JwtToken jwtToken = new JwtToken();
	    jwtToken.setToken(token);
	    jwtToken.setUser(user);
	    jwtToken.setCreatedAt(LocalDateTime.now());
	    jwtToken.setExpiresAt(LocalDateTime.now().plus(jwtProperties.getExpiration(), ChronoUnit.MILLIS));
	    jwtTokenRepository.save(jwtToken);
	}
	
	public ResponseEntity<?> validateToken(String token, User user, Claims claims, String roleName) {
		// Validate token structure and signature
        Date jwtExpiration = claims.getExpiration();

        // Check token in DB
        Optional<JwtToken> tokenEntryOpt = jwtTokenRepository.findByToken(token);
        if (tokenEntryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token not recognized");
        }
        
        JwtToken tokenEntry = tokenEntryOpt.get();
        
        LocalDateTime now = LocalDateTime.now();

		// Convert jwtExpiration (java.util.Date) to LocalDateTime
		LocalDateTime jwtExp = jwtExpiration
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();
		
		// tokenEntry.getExpiresAt() is already LocalDateTime
		LocalDateTime dbExp = tokenEntry.getExpiresAt();
		
		// Now compare both to now
		// Compare DB expiration and token expiration
		if (jwtExp.isBefore(now) || dbExp.isBefore(now)) {
            // Expired → delete all tokens under this user
        	jwtTokenRepository.deleteAllByUser(user);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired. All sessions cleared.");
        }

        return ResponseEntity.ok(new ValidationRequest(
        	user.getId(),
        	roleName,
        	"Token is valid"
        ));
	}
	
	public void deleteAllTokensByUser(User user) {
	    jwtTokenRepository.deleteAllByUser(user);
	}
}
