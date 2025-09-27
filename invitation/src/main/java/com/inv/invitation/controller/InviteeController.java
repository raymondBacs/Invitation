package com.inv.invitation.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inv.invitation.model.Invitee;
import com.inv.invitation.dto.InviteeRequest;
import com.inv.invitation.model.Invitation;
import com.inv.invitation.model.InviteeType;
import com.inv.invitation.model.User;
import com.inv.invitation.service.InviteeService;
import com.inv.invitation.service.InviteeTypeService;
import com.inv.invitation.service.PublicInvitationDetailService;
import com.inv.invitation.service.InvitationService;
import com.inv.invitation.service.UserService;

@RestController
@RequestMapping("/api/invitees")
@CrossOrigin(origins = "http://localhost:3000")
public class InviteeController {

    private final InviteeService inviteeService;
    private final InvitationService invitationService;
    private final InviteeTypeService inviteeTypeService;
    private final UserService userService;
    private final PublicInvitationDetailService publicInvitationDetailService;

    public InviteeController(InviteeService inviteeService, InvitationService invitationService, InviteeTypeService inviteeTypeService, UserService userService, PublicInvitationDetailService publicInvitationDetailService) {
        this.inviteeService = inviteeService;
        this.invitationService = invitationService;
        this.inviteeTypeService = inviteeTypeService;
        this.userService = userService;
        this.publicInvitationDetailService = publicInvitationDetailService;
    }

    @GetMapping("/by-invitation/{invitationId}")
    public ResponseEntity<?> listByInvitation(@RequestHeader("Authorization") String authHeader, @PathVariable Long invitationId) {
        try {
        	if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
            String token = authHeader.substring(7);
            
            Optional<User> userOpt = userService.getUserFromToken(token);
            Optional<Invitation> invOpt = invitationService.findById(invitationId);
            List<Invitee> list = inviteeService.listByInvitation(invOpt.get());
            
            if(!invOpt.get().getAccount().getId().equals(userOpt.get().getId()) && !userService.isAdmin(userOpt.get())) {
            	return ResponseEntity.status(403).body("Not allowed");
            } else {
            	return ResponseEntity.ok(list);
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("Authorization") String authHeader, @RequestBody InviteeRequest payload) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        if (caller.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        
        // validate invitation exists
        Optional<Invitation> invitationOpt = invitationService.findById(payload.getInvitationId());

        // ensure invitee type exists if provided
        InviteeType inviteeType = inviteeTypeService.findById(payload.getInviteeTypeId());
        Invitee invitee = new Invitee();
        
        invitee.setFirstName(payload.getFirstName());
        invitee.setLastName(payload.getLastName());
        invitee.setContact(payload.getContact());
        invitee.setEmail(payload.getEmail());
        invitee.setImage(payload.getImage());
        invitee.setInviteeType(inviteeType);
        invitee.setInvitation(invitationOpt.get());
        invitee.setCreatedBy(caller.get());
        
        invitee = inviteeService.create(invitee);
        
        publicInvitationDetailService.addPublicInvitationDetailByInvitee(invitee, invitationOpt.get());
        
        return ResponseEntity.ok(invitee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody InviteeRequest payload) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);

        try {
        	InviteeType inviteeType = inviteeTypeService.findById(payload.getInviteeTypeId());
        	
        	Invitee invitee = new Invitee();
            
        	invitee.setFirstName(payload.getFirstName());
            invitee.setLastName(payload.getLastName());
            invitee.setContact(payload.getContact());
            invitee.setEmail(payload.getEmail());
            invitee.setImage(payload.getImage());
            invitee.setInviteeType(inviteeType);
            invitee.setModifiedBy(caller.get());
        	
            invitee = inviteeService.update(id, invitee);
            return ResponseEntity.ok(invitee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional.ofNullable(userService.getUserFromToken(token)).orElseGet(Optional::empty);

        inviteeService.softDelete(id);
        return ResponseEntity.ok("Deleted");
    }
}
