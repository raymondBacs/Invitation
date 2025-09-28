package com.inv.invitation.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inv.invitation.dto.InvitationRequest;
import com.inv.invitation.dto.InvitationResponse;
import com.inv.invitation.model.Invitation;
import com.inv.invitation.model.InvitationDetail;
import com.inv.invitation.model.InvitationType;
import com.inv.invitation.model.User;
import com.inv.invitation.service.InvitationDetailService;
import com.inv.invitation.service.InvitationService;
import com.inv.invitation.service.InvitationTypeService;
import com.inv.invitation.service.UserService;
import com.inv.invitation.util.InvitationDetailUtil;

@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "http://localhost:3000")
public class InvitationController {

    private final InvitationService invitationService;
    private final InvitationTypeService invitationTypeService;
    private final UserService userService;
    private final InvitationDetailService invitationDetailService;
    private final InvitationDetailUtil invitationDetailUtil;

    public InvitationController(InvitationService invitationService, InvitationTypeService invitationTypeService, 
    		UserService userService, InvitationDetailService invitationDetailService, InvitationDetailUtil invitationDetailUtil) {
        this.invitationService = invitationService;
        this.invitationTypeService = invitationTypeService;
        this.userService = userService;
        this.invitationDetailService = invitationDetailService;
        this.invitationDetailUtil = invitationDetailUtil;
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
    	InvitationDetail invitationDetail = new InvitationDetail();
    	
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        Optional<User> userOpt = userService.getById(payload.getAccountId());
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");        

        // If client provided an invitationType id (in payload.invitationType.id), ensure it's a managed type
        InvitationType type = invitationTypeService.findById(payload.getInvitationType().getId());
        if (type != null && type.getId() != null) {
            InvitationType managed = invitationTypeService.findById(type.getId());
            if (managed == null) return ResponseEntity.badRequest().body("InvitationType not found");
            invitation.setInvitationType(managed);
        }
        invitation.setCreatedBy(caller.get());
        invitation.setAccount(userOpt.get());
        invitation = invitationService.create(invitation);
        
        invitationDetail.setEventTitle(payload.getEventTitle());
        invitationDetail.setIntroduction(payload.getIntroduction());
        invitationDetail.setEvent_date(payload.getEventDate());
        invitationDetail.setDateLocked(payload.getDateLocked());
        invitationDetail.setLockedDaysBeforeEvent(payload.getLockedDaysBeforeEvent());
        invitationDetail.setDressCode(payload.getDressCode());
        invitationDetail.setTheme(payload.getTheme());
        invitationDetail.setEventStartTime(payload.getEventStartTime());
        invitationDetail.setEventEndTime(payload.getEventEndTime());
        invitationDetail.setHasGiftPreference(payload.getHasGiftPreference());
        invitationDetail.setSpecialInstructions(payload.getSpecialInstructions());
        invitationDetail = invitationDetailService.saved(invitationDetail);
        
        InvitationResponse invitationResponse = InvitationResponse.fromEntities(invitation, invitationDetail);
        
        return ResponseEntity.ok(invitationResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");
        
        Optional<Invitation> invitationOpt = invitationService.findById(id);
        if(invitationOpt.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invitation Not Found");
        }
        Invitation invitation = invitationOpt.get();
        
        if (!invitation.getAccount().getId().equals(caller.get().getId()) && !userService.isAdmin(caller.get())) {
            return ResponseEntity.status(403).body("Not allowed");
        }
        
        Optional<InvitationDetail> invitationDetailOpt = invitationDetailService.getInvitationDetailByInvitationId(invitation.getId());
        if(invitationDetailOpt.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No invitation detail found");
        }
        InvitationDetail invitationDetail = invitationDetailOpt.get();
        Boolean isEventDone = invitationDetailUtil.isEventDone(invitationDetail);
        
        if(isEventDone) {
        	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Event is Done.");
        }
        
        return ResponseEntity.ok(InvitationResponse.fromEntities(invitation, invitationDetail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody InvitationRequest payload) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");

        try {
            // ensure invitation type is managed if provided
            InvitationType type = invitationTypeService.findById(payload.getInvitationType().getId());
            if (type != null && type.getId() != null) {
            	return ResponseEntity.badRequest().body("InvitationType not found");
            }
            
            Optional<InvitationDetail> invitationDetailOpt = invitationDetailService.getInvitationDetailByInvitationId(payload.getInvitationId());
            if(invitationDetailOpt.isEmpty()) {
            	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No invitation detail found");
            }
            InvitationDetail invitationDetailObj = invitationDetailOpt.get();
            Boolean isEventDone = invitationDetailUtil.isEventDone(invitationDetailObj);
            
            if(isEventDone) {
            	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Event is Done.");
            }
            
            Invitation invitation = invitationService.update(id, payload, caller.get(), type);
            
            InvitationDetail invitationDetail = new InvitationDetail();
            invitationDetail.setEventTitle(payload.getEventTitle());
            invitationDetail.setIntroduction(payload.getIntroduction());
            invitationDetail.setEvent_date(payload.getEventDate());
            invitationDetail.setDateLocked(payload.getDateLocked());
            invitationDetail.setLockedDaysBeforeEvent(payload.getLockedDaysBeforeEvent());
            invitationDetail.setDressCode(payload.getDressCode());
            invitationDetail.setTheme(payload.getTheme());
            invitationDetail.setEventStartTime(payload.getEventStartTime());
            invitationDetail.setEventEndTime(payload.getEventEndTime());
            invitationDetail.setHasGiftPreference(payload.getHasGiftPreference());
            invitationDetail.setSpecialInstructions(payload.getSpecialInstructions());
            invitationDetail = invitationDetailService.saved(invitationDetail);
            
            InvitationResponse invitationResponse = InvitationResponse.fromEntities(invitation, invitationDetail);
            
            return ResponseEntity.ok(invitationResponse);
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
        
        Optional<Invitation> invitationOpt = invitationService.findById(id);
        if(invitationOpt.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invitation Not Found");
        }
        Invitation invitation = invitationOpt.get();
        
        Optional<InvitationDetail> invitationDetailOpt = invitationDetailService.getInvitationDetailByInvitationId(invitation.getId());
        if(invitationDetailOpt.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No invitation detail found");
        }
        InvitationDetail invitationDetail = invitationDetailOpt.get();
        Boolean isEventDone = invitationDetailUtil.isEventDone(invitationDetail);
        
        if(isEventDone) {
        	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Event is Done.");
        }
        
        if (!invitation.getAccount().getId().equals(caller.get().getId()) && !userService.isAdmin(caller.get())) {
            return ResponseEntity.status(403).body("Not allowed");
        }
        
        invitationService.softDelete(id);
        return ResponseEntity.ok("Deleted");
    }
}
