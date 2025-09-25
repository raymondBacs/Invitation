package com.inv.invitation.controller;

import com.inv.invitation.model.InvitationNotification;
import com.inv.invitation.repository.InvitationNotificationRepository;
import com.inv.invitation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class InvitationNotificationController {

    private final InvitationNotificationRepository repo;
    private final JwtUtil jwtUtil;

    // ✅ Utility method to validate token and extract user email
    private String validateToken(String authHeader) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.validateToken(token);
        return claims.getSubject(); // user email from token
    }

    @GetMapping("/invitation/{invitationId}")
    public ResponseEntity<List<InvitationNotification>> getByInvitation(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long invitationId) {
        try {
            validateToken(authHeader); // ✅ ensures only authenticated access
            return ResponseEntity.ok(repo.findByInvitationId(invitationId));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/{id}/seen")
    public ResponseEntity<InvitationNotification> markSeen(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            validateToken(authHeader); // ✅ authentication check
            return repo.findById(id).map(n -> {
                n.setSeen(true);
                return ResponseEntity.ok(repo.save(n));
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}
