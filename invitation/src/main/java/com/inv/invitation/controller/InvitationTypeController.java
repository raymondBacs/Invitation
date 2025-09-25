package com.inv.invitation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inv.invitation.model.InvitationType;
import com.inv.invitation.service.InvitationTypeService;
import com.inv.invitation.service.UserService;

@RestController
@RequestMapping("/api/invitation-types")
@CrossOrigin(origins = "http://localhost:3000")
public class InvitationTypeController {

    private final InvitationTypeService service;
    private final UserService userService;

    public InvitationTypeController(InvitationTypeService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<InvitationType>> list(@RequestHeader("Authorization") String authHeader) {
        // allow any authenticated user to view types
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        String token = authHeader.substring(7);
        return userService.getUserFromToken(token)
                .map(u -> ResponseEntity.ok(service.listAll()))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("Authorization") String authHeader, @RequestBody InvitationType type) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        return userService.getUserFromToken(token)
                .map(u -> {
                    if (!userService.isAdmin(u)) return ResponseEntity.status(403).body("Admin access required.");
                    return ResponseEntity.ok(service.create(type));
                })
                .orElseGet(() -> ResponseEntity.status(401).body("Invalid token."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody InvitationType type) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        return userService.getUserFromToken(token)
                .map(u -> {
                    if (!userService.isAdmin(u)) return ResponseEntity.status(403).body("Admin access required.");
                    try {
                        return ResponseEntity.ok(service.update(id, type));
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(404).body(e.getMessage());
                    }
                })
                .orElseGet(() -> ResponseEntity.status(401).body("Invalid token."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        return userService.getUserFromToken(token)
                .map(u -> {
                    if (!userService.isAdmin(u)) return ResponseEntity.status(403).body("Admin access required.");
                    service.delete(id);
                    return ResponseEntity.ok("Deleted");
                })
                .orElseGet(() -> ResponseEntity.status(401).body("Invalid token."));
    }
}
