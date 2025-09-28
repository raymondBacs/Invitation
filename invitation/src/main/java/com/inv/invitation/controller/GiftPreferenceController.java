package com.inv.invitation.controller;

import com.inv.invitation.dto.GiftPreferenceResponse;
import com.inv.invitation.model.GiftPreference;
import com.inv.invitation.model.Invitation;
import com.inv.invitation.model.User;
import com.inv.invitation.service.GiftPreferenceService;
import com.inv.invitation.service.InvitationService;
import com.inv.invitation.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gift-preferences")
public class GiftPreferenceController {

    private final GiftPreferenceService giftPreferenceService;
    private final UserService userService;
    private final InvitationService invitationService;

    public GiftPreferenceController(GiftPreferenceService giftPreferenceService, UserService userService,
    		InvitationService invitationService) {
        this.giftPreferenceService = giftPreferenceService;
        this.userService = userService;
        this.invitationService = invitationService;
    }

    @PostMapping
    public ResponseEntity<?> createGiftPreference(
            @RequestBody GiftPreferenceResponse giftPreferenceResponse,
            @RequestHeader("Authorization") String authHeader) {
    	if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");
        
        Optional<Invitation> invitationOpt = invitationService.findById(giftPreferenceResponse.getInvitationId());
        if(invitationOpt.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invitation Not Found");
        }
        Invitation invitation = invitationOpt.get();
        
        GiftPreference giftPreference = new GiftPreference();
        giftPreference.setInvitation(invitation);
        giftPreference.setGiftType(giftPreferenceResponse.getGiftType());
        giftPreference.setDescription(giftPreferenceResponse.getDescription());
        giftPreference.setLink(giftPreferenceResponse.getLink());
        giftPreference.setPriority(giftPreferenceResponse.getPriority());
    	
        return ResponseEntity.ok(giftPreferenceService.createGiftPreference(giftPreference));
    }

    @PutMapping()
    public ResponseEntity<?> updateGiftPreference(
            @RequestBody GiftPreferenceResponse giftPreferenceResponse,
            @RequestHeader("Authorization") String authHeader) {
    	if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");
        
        return ResponseEntity.ok(giftPreferenceService.updateGiftPreference(giftPreferenceResponse.getId(), giftPreferenceResponse));
    }

    @GetMapping("/invitation/{invitationId}")
    public ResponseEntity<List<GiftPreference>> getGiftPreferences(@PathVariable Long invitationId) {
        return ResponseEntity.ok(giftPreferenceService.getGiftPreferencesByInvitation(invitationId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGiftPreference(@PathVariable Long id,
    		@RequestHeader("Authorization") String authHeader) {
    	if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");
        
        giftPreferenceService.deleteGiftPreference(id);
        return ResponseEntity.noContent().build();
    }
}