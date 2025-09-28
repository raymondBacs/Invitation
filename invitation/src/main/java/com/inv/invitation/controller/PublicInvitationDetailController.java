package com.inv.invitation.controller;

import com.inv.invitation.dto.InvitationFormFieldResponse;
import com.inv.invitation.dto.InviteeSummaryResponse;
import com.inv.invitation.dto.PublicInvitationAuth;
import com.inv.invitation.dto.PublicInvitationDetailRequest;
import com.inv.invitation.dto.PublicInvitationDetailResponse;
import com.inv.invitation.model.*;
import com.inv.invitation.repository.*;
import com.inv.invitation.service.DietaryTypeService;
import com.inv.invitation.service.InvitationDetailService;
import com.inv.invitation.service.InvitationService;
import com.inv.invitation.service.PublicInvitationDetailService;
import com.inv.invitation.service.RSVPResponseTypeService;
import com.inv.invitation.service.UserService;
import com.inv.invitation.util.InvitationDetailUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/public-invitation")
@RequiredArgsConstructor
public class PublicInvitationDetailController {
    private final PublicInvitationDetailRepository repo;
    private final InvitationNotificationRepository notifRepo;
    private final RSVPResponseTypeRepository rSVPResponseTypeRepository;
    private final DietaryTypeRepository dietaryTypeRepository;
    private final InvitationFormFieldRepository invitationFormFieldRepository;
    
    private final PublicInvitationDetailService publicInvitationDetailService;
    private final InvitationService invitationService;
    private final RSVPResponseTypeService rSVPResponseTypeService;
    private final DietaryTypeService dietaryTypeService;
    private final UserService userService;
    private final InvitationDetailService invitationDetailService;
    private final InvitationDetailUtil invitationDetailUtil;

    @GetMapping("/{invitationCode}/{first}-{last}")
    public ResponseEntity<?> openInvitation(
            @PathVariable String invitationCode,
            @PathVariable String first,
            @PathVariable String last,
            @RequestBody PublicInvitationAuth publicInvitationAuth) {
    	
    	
    	ResponseEntity<?> invitationCodeResp = invitationService.authenticate16DigitCode(invitationCode);
    	if (!invitationCodeResp.getStatusCode().is2xxSuccessful()) {
    	    return invitationCodeResp;
    	}
    	Invitation invitationObj = (Invitation) invitationCodeResp.getBody();
    	
    	Optional<InvitationDetail> invitationDetailOpt = invitationDetailService.getInvitationDetailByInvitationId(invitationObj.getId());
        if(invitationDetailOpt.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No invitation detail found");
        }
        InvitationDetail invitationDetail = invitationDetailOpt.get();
        Boolean isEventDone = invitationDetailUtil.isEventDone(invitationDetail);
        
        if(isEventDone || invitationDetail.getDateLocked()) {
        	InviteeSummaryResponse inviteeSummaryResponse = new InviteeSummaryResponse();
        	return ResponseEntity.ok(inviteeSummaryResponse);
        }
    	
    	ResponseEntity<?> inviteeCodeResp = publicInvitationDetailService.authenticate8DigitCode(publicInvitationAuth.getCode());
    	if (!inviteeCodeResp.getStatusCode().is2xxSuccessful()) {
    	    return inviteeCodeResp;
    	}
    	
    	if(invitationCodeResp.getStatusCode().is2xxSuccessful() && inviteeCodeResp.getStatusCode().is2xxSuccessful()) {
    		return getPublicInvitationDetailResponseValidated(publicInvitationAuth.getCode());
    	} else {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request");
    	}
    }
    
    private ResponseEntity<?> getPublicInvitationDetailResponseValidated(String code) {
    	return repo.findByAuthCode(code).map(detail -> {
            detail.setSeen(true);
            repo.save(detail);
            
            List<InvitationFormField> invitationFormField = invitationFormFieldRepository.findByInvitationTypeId(detail.getInvitation().getId());
            List<RSVPResponseType> rSVPResponseTypeList = rSVPResponseTypeService.getAllRSVPResponseType();
            List<DietaryType> dietaryTypeList = dietaryTypeService.getAllDietaryType();
            
            List<InvitationFormFieldResponse> invitationFormFieldResponseList = new ArrayList<InvitationFormFieldResponse>();
            for(InvitationFormField currentInvitationFormField : invitationFormField) {
            	InvitationFormFieldResponse invitationFormFieldResponse = new InvitationFormFieldResponse();
            	
            	invitationFormFieldResponse.setRequired(currentInvitationFormField.isRequired());
            	invitationFormFieldResponse.setActive(currentInvitationFormField.isActive());
            	invitationFormFieldResponse.setFieldName(currentInvitationFormField.getFieldName());
            	invitationFormFieldResponse.setLabel(currentInvitationFormField.getLabel());
            	invitationFormFieldResponse.setFieldType(currentInvitationFormField.getFieldType());
            	
            	invitationFormFieldResponseList.add(invitationFormFieldResponse);
            }
            
            Optional<InvitationDetail> invitationDetailOpt = invitationDetailService.getInvitationDetailByInvitationId(detail.getInvitation().getId());
            if(invitationDetailOpt.isEmpty()) {
            	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No invitation detail found");
            }
            InvitationDetail invitationDetail = invitationDetailOpt.get();
            
            PublicInvitationDetailResponse returnObj = new PublicInvitationDetailResponse();
            returnObj.setId(detail.getId());
            returnObj.setFirstName(detail.getInvitee().getFirstName());
            returnObj.setLastName(detail.getInvitee().getLastName());
            returnObj.setRsvpResponse(detail.getRsvpResponse()==null ? "" : detail.getRsvpResponse().getName());
            returnObj.setLockedDaysBeforeEvent(invitationDetail.getLockedDaysBeforeEvent());
            returnObj.setInviteeCode(detail.getAuthCode());
            returnObj.setInvitationCode(detail.getInvitation().getInvitationCode());
            returnObj.setEventDate(invitationDetail.getEvent_date());
            
            returnObj.setGuestCount(detail.getGuestCount() == null ? 0 : detail.getGuestCount());
            returnObj.setDietaryType(detail.getDietaryType()==null ? "" : detail.getDietaryType().getName());
            returnObj.setDietaryTypeNote(detail.getDietaryTypeNote());
            returnObj.setAccessibility(detail.getAccessibility());
            returnObj.setNotes(detail.getNotes());
            
            returnObj.setArrivalDate(detail.getArrivalDate());
            returnObj.setEmail(detail.getEmail());
            returnObj.setContact(detail.getContact());
            returnObj.setOrganization(detail.getOrganization());
            returnObj.setSessionSelection(detail.getSessionSelection());
            returnObj.setTransportation(detail.getTransportation());
            returnObj.setSpecialRequests(detail.getSpecialRequests());
            returnObj.setVipSeating(detail.getVipSeating());
            returnObj.setRole(detail.getRole());
            returnObj.setPerformanceRole(detail.getPerformanceRole());
            returnObj.setAvailability(detail.getAvailability());
            returnObj.setMediaOutlet(detail.getMediaOutlet());
            returnObj.setPressId(detail.getPressId());
            returnObj.setEntourageRole(detail.getEntourageRole());
            returnObj.setCompany(detail.getCompany());
            returnObj.setDesignation(detail.getDesignation());
            
            returnObj.setInvitationFormField(invitationFormFieldResponseList);
            returnObj.setDietaryTypeList(dietaryTypeList);
            returnObj.setRSVPResponseTypeList(rSVPResponseTypeList);
            
            return ResponseEntity.ok(returnObj);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitForm(@RequestBody PublicInvitationDetailRequest submission) {
    	ResponseEntity<?> invitationCodeResp = invitationService.authenticate16DigitCode(submission.getInvitationCode());
    	ResponseEntity<?> inviteeCodeResp = publicInvitationDetailService.authenticate8DigitCode(submission.getInviteeCode());
    	
    	if(!invitationCodeResp.getStatusCode().is2xxSuccessful() && !inviteeCodeResp.getStatusCode().is2xxSuccessful()) {
    		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication Failed");
    	}
    	Invitation invitationObj = (Invitation) invitationCodeResp.getBody();
    	
    	Optional<InvitationDetail> invitationDetailOpt = invitationDetailService.getInvitationDetailByInvitationId(invitationObj.getId());
        if(invitationDetailOpt.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No invitation detail found");
        }
        InvitationDetail invitationDetail = invitationDetailOpt.get();
        Boolean isEventDone = invitationDetailUtil.isEventDone(invitationDetail);
        
        if(isEventDone) {
        	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Event is Done.");
        }
        
        if(invitationDetail.getDateLocked()) {
        	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Response is locked");
        }
    	
        return repo.findByAuthCode(submission.getInviteeCode()).map(existing -> {
        	PublicInvitationDetailRequest oldData = populatePublicInvitationDetailRequestFromDatabase(existing);
        	
        	if(!hasChanges(oldData, submission)) {
        		return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("No changes detected");
        	}
        	
        	String summarizeChanges_notification = summarizeChanges(oldData, submission);
        	
        	Optional<RSVPResponseType> rSVPResponseTypeOpt = rSVPResponseTypeRepository.findByName(submission.getRsvpResponse());
        	if (rSVPResponseTypeOpt.isEmpty()) {
                return ResponseEntity.status(404).body("RSVP not found");
            }
        	RSVPResponseType rSVPResponseType = rSVPResponseTypeOpt.get();
        	
        	Optional<DietaryType> dietaryTypeOpt = dietaryTypeRepository.findByName(submission.getDietaryType());
        	DietaryType dietaryType = null;
        	if (!dietaryTypeOpt.isEmpty()) {
        		dietaryType = dietaryTypeOpt.get();
            }
        	
        	existing.setRsvpResponse(rSVPResponseType);
        	existing.setGuestCount(submission.getGuestCount());
        	existing.setArrivalDate(submission.getArrivalDate());
        	existing.setDietaryType(dietaryType);
        	existing.setDietaryTypeNote(submission.getDietaryTypeNote());
        	existing.setNotes(submission.getNotes());
        	
        	existing.setEmail(submission.getEmail());
            existing.setContact(submission.getContact());
            existing.setOrganization(submission.getOrganization());
            existing.setSessionSelection(submission.getSessionSelection());
            existing.setTransportation(submission.getTransportation());
            existing.setSpecialRequests(submission.getSpecialRequests());
            existing.setVipSeating(submission.getVipSeating());
            existing.setRole(submission.getRole());
            existing.setPerformanceRole(submission.getPerformanceRole());
            existing.setAvailability(submission.getAvailability());
            existing.setMediaOutlet(submission.getMediaOutlet());
            existing.setPressId(submission.getPressId());
            existing.setEntourageRole(submission.getEntourageRole());
            existing.setCompany(submission.getCompany());
            existing.setDesignation(submission.getDesignation());
            existing.setAccessibility(submission.getAccessibility());
            
            PublicInvitationDetail saved = repo.save(existing);

            InvitationNotification notif = new InvitationNotification();
            notif.setInvitation(saved.getInvitation());
            notif.setPublicInvitationDetail(saved);
            notif.setMessage(summarizeChanges_notification);
            notifRepo.save(notif);

            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    private PublicInvitationDetailRequest populatePublicInvitationDetailRequestFromDatabase(PublicInvitationDetail detail) {
    	PublicInvitationDetailRequest returnObj = new PublicInvitationDetailRequest();
    	
    	returnObj.setFirstName(detail.getInvitee().getFirstName());
    	returnObj.setLastName(detail.getInvitee().getLastName());
    	returnObj.setInvitationCode(detail.getInvitation().getInvitationCode());
    	returnObj.setInviteeCode(detail.getAuthCode());
    	
    	returnObj.setRsvpResponse(detail.getRsvpResponse()==null ? null : detail.getRsvpResponse().getName());
    	returnObj.setGuestCount(detail.getGuestCount());
    	returnObj.setArrivalDate(detail.getArrivalDate());
    	returnObj.setDietaryType(detail.getDietaryType()==null ? null : detail.getDietaryType().getName());
    	returnObj.setDietaryTypeNote(detail.getDietaryTypeNote());
    	returnObj.setNotes(detail.getNotes());
    	
    	returnObj.setEmail(detail.getEmail());
    	returnObj.setContact(detail.getContact());
    	returnObj.setOrganization(detail.getOrganization());
        returnObj.setSessionSelection(detail.getSessionSelection());
        returnObj.setTransportation(detail.getTransportation());
        returnObj.setSpecialRequests(detail.getSpecialRequests());
        returnObj.setVipSeating(detail.getVipSeating());
        returnObj.setRole(detail.getRole());
        returnObj.setPerformanceRole(detail.getPerformanceRole());
        returnObj.setAvailability(detail.getAvailability());
        returnObj.setMediaOutlet(detail.getMediaOutlet());
        returnObj.setPressId(detail.getPressId());
        returnObj.setEntourageRole(detail.getEntourageRole());
        returnObj.setCompany(detail.getCompany());
        returnObj.setDesignation(detail.getDesignation());
        returnObj.setAccessibility(detail.getAccessibility());
        
        return returnObj;
    }

    public String summarizeChanges(PublicInvitationDetailRequest oldData, PublicInvitationDetailRequest newData) {
        if (oldData == null || newData == null) {
            return "We could not check updates because some details are missing.";
        }

        List<String> changes = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try {
            Field[] fields = PublicInvitationDetailRequest.class.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object oldValue = field.get(oldData);
                Object newValue = field.get(newData);

                String oldStr = (oldValue == null) ? "" : oldValue.toString();
                String newStr = (newValue == null) ? "" : newValue.toString();

                if (oldValue instanceof java.time.LocalDateTime) {
                    oldStr = ((java.time.LocalDateTime) oldValue).format(dtf);
                }
                if (newValue instanceof java.time.LocalDateTime) {
                    newStr = ((java.time.LocalDateTime) newValue).format(dtf);
                }

                if (!oldStr.equals(newStr)) {
                    String label = toFriendlyLabel(field.getName());
                    changes.add(String.format("%s updated to \"%s\"", label, newStr));
                }
            }
        } catch (IllegalAccessException e) {
            return "We couldn’t check updates due to a system error.";
        }

        if (changes.isEmpty()) {
            return "No updates were made by the invitee.";
        }

        return oldData.getFirstName() + " " + oldData.getLastName() + " has shared updates to their invitation details. " + String.join(", ", changes) + ".";
    }
    
    public boolean hasChanges(PublicInvitationDetailRequest oldData, PublicInvitationDetailRequest newData) {
        if (oldData == null || newData == null) {
            return false; // if either is null, treat as no change (or you could decide to return true)
        }

        try {
            Field[] fields = PublicInvitationDetailRequest.class.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object oldValue = field.get(oldData);
                Object newValue = field.get(newData);

                if (oldValue == null && newValue != null) {
                    return true;
                }
                if (oldValue != null && newValue == null) {
                    return true;
                }
                if (oldValue != null && !oldValue.equals(newValue)) {
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error checking changes: " + e.getMessage(), e);
        }

        return false; // no differences found
    }

    private String toFriendlyLabel(String fieldName) {
        switch (fieldName) {
            case "rsvpResponse": return "RSVP response";
            case "guestCount": return "Guest count";
            case "dietaryType": return "Dietary preference";
            case "dietaryTypeNote": return "Dietary notes";
            case "accessibility": return "Accessibility needs";
            case "arrivalDate": return "Arrival date";
            case "contact": return "Contact number";
            case "organization": return "Organization";
            case "specialRequests": return "Special requests";
            case "vipSeating": return "VIP seating";
            case "role": return "Role";
            case "performanceRole": return "Performance role";
            case "availability": return "Availability";
            case "mediaOutlet": return "Media outlet";
            case "pressId": return "Press ID";
            case "entourageRole": return "Entourage role";
            case "company": return "Company";
            case "designation": return "Designation";
            default: return capitalize(fieldName);
        }
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    @PutMapping("/regeneratePublicInviteeCode/{id}")
    public ResponseEntity<?> updateCode(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
    	if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");
        
        return publicInvitationDetailService.updateCode(id);
    }
    
    @GetMapping("/generatePublicURLExistingInvitee/{inviteeId}")
    public ResponseEntity<?> generatePublicURLExistingInvitee(@RequestHeader("Authorization") String authHeader,
    		@PathVariable Long inviteeId, HttpServletRequest request) {
    	
    	if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Invalid token format.");
        String token = authHeader.substring(7);
        Optional<User> caller = userService.getUserFromToken(token);
        if (caller.isEmpty()) return ResponseEntity.status(401).body("Invalid token.");
        
        return publicInvitationDetailService.generatePublicInvitationURLExistingInvitee(inviteeId, request);
    }
}
