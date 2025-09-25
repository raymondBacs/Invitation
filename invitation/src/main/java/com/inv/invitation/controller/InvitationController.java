package com.inv.invitation.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inv.invitation.dto.InvitationRequest;
import com.inv.invitation.model.Invitation;
import com.inv.invitation.model.InvitationType;
import com.inv.invitation.model.User;
import com.inv.invitation.service.InvitationService;
import com.inv.invitation.service.InvitationTypeService;
import com.inv.invitation.service.UserService;

@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "http://localhost:3000")
public class InvitationController {

    private final InvitationService invitationService;
    private final InvitationTypeService invitationTypeService;
    private final UserService userService;

    public InvitationController(InvitationService invitationService, InvitationTypeService invitationTypeService, UserService userService) {
        this.invitationService = invitationService;
        this.invitationTypeService = invitationTypeService;
        this.userService = userService;
    }
    
    @GetMapping("/list/{id}")
    public ResponseEntity<?> list(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        Optional<User> userOpt = userService.getById(id);
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");
        // Return invitations belonging to the account (caller)
        List<Invitation> list = invitationService.listByAccount(userOpt.get());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("Authorization") String authHeader, @RequestBody InvitationRequest payload) {
    	Invitation invitation = new Invitation();
    	
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        Optional<User> userOpt = userService.getById(payload.getAccountId());
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");

        // If client provided an invitationType id (in payload.invitationType.id), ensure it's a managed type
        InvitationType type = invitationTypeService.findById(payload.getInvitationTypeId());
        if (type != null && type.getId() != null) {
            InvitationType managed = invitationTypeService.findById(type.getId());
            if (managed == null) return ResponseEntity.badRequest().body("InvitationType not found");
            invitation.setInvitationType(managed);
        }
        
        invitation.setTitle(payload.getTitle());
        invitation.setMessage(payload.getMessage());
        invitation.setCreatedBy(caller.get());
        invitation.setAccount(userOpt.get());
        Invitation saved = invitationService.create(invitation);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");
        return invitationService.findById(id)
                .map(i -> {
                    if (!i.getAccount().getId().equals(caller.get().getId()) && !userService.isAdmin(caller.get()))
                        return ResponseEntity.status(403).body("Not allowed");
                    return ResponseEntity.ok(i);
                })
                .orElseGet(() -> ResponseEntity.status(404).body("Invitation not found"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody InvitationRequest payload) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");

        try {
            // ensure invitation type is managed if provided
            InvitationType type = invitationTypeService.findById(payload.getInvitationTypeId());
            Invitation updated = invitationService.update(id, payload, caller.get(), type);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");

        // allow deletion only by owner or admin
        return invitationService.findById(id)
                .map(i -> {
                    if (!i.getAccount().getId().equals(caller.get().getId()) && !userService.isAdmin(caller.get()))
                        return ResponseEntity.status(403).body("Not allowed");
                    invitationService.softDelete(id);
                    return ResponseEntity.ok("Deleted");
                })
                .orElseGet(() -> ResponseEntity.status(404).body("Invitation not found"));
    }
}
