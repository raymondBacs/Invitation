package com.inv.invitation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inv.invitation.model.InviteeType;
import com.inv.invitation.service.InviteeTypeService;
import com.inv.invitation.service.UserService;

@RestController
@RequestMapping("/api/invitee-types")
@CrossOrigin(origins = "http://localhost:3000")
public class InviteeTypeController {

    private final InviteeTypeService service;
    private final UserService userService;

    public InviteeTypeController(InviteeTypeService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<InviteeType>> list(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().build();
        String token = authHeader.substring(7);
        return userService.getUserFromToken(token)
                .map(u -> ResponseEntity.ok(service.listAll()))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("Authorization") String authHeader, @RequestBody InviteeType t) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        return userService.getUserFromToken(token)
                .map(u -> {
                    if (!userService.isAdmin(u)) return ResponseEntity.status(403).body("Admin access required.");
                    return ResponseEntity.ok(service.create(t));
                })
                .orElseGet(() -> ResponseEntity.status(401).body("Invalid token."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody InviteeType t) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        return userService.getUserFromToken(token)
                .map(u -> {
                    if (!userService.isAdmin(u)) return ResponseEntity.status(403).body("Admin access required.");
                    try {
                        return ResponseEntity.ok(service.update(id, t));
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
