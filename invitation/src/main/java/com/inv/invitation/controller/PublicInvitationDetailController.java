package com.inv.invitation.controller;

import com.inv.invitation.dto.InvitationFormFieldResponse;
import com.inv.invitation.dto.PublicInvitationDetailRequest;
import com.inv.invitation.dto.PublicInvitationDetailResponse;
import com.inv.invitation.model.*;
import com.inv.invitation.repository.*;
import com.inv.invitation.service.PublicInvitationDetailService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public-invitation")
@RequiredArgsConstructor
public class PublicInvitationDetailController {
    private final PublicInvitationDetailRepository repo;
    private final InvitationNotificationRepository notifRepo;
    private final InvitationRepository invitationRepo;
    private final InviteeRepository inviteeRepo;
    private final RSVPResponseTypeRepository rSVPResponseTypeRepository;
    private final DietaryTypeRepository dietaryTypeRepository;
    private final PublicInvitationDetailService publicInvitationDetailService;
    private final InvitationFormFieldRepository invitationFormFieldRepository;

    @GetMapping("/{code}/{first}-{last}")
    public ResponseEntity<?> openInvitation(
            @PathVariable String code,
            @PathVariable String first,
            @PathVariable String last) {
        return repo.findByInvitationCode(code).map(detail -> {
            detail.setSeen(true);
            repo.save(detail);
            
            List<InvitationFormField> invitationFormField = invitationFormFieldRepository.findByInvitationTypeId(detail.getInvitation().getId());
            
            PublicInvitationDetailResponse returnObj = new PublicInvitationDetailResponse();
            returnObj.setInvitationId(detail.getInvitation().getId());
            returnObj.setInviteeId(detail.getInvitee().getId());
            returnObj.setGuestCount(detail.getGuestCount() == null ? 0 : detail.getGuestCount());
            returnObj.setInvitationCode(detail.getInvitationCode());
            returnObj.setRsvpResponse(detail.getRsvpResponse()==null ? "" : detail.getRsvpResponse().getName());
            
            returnObj.setId(detail.getId());
            returnObj.setSeen(detail.isSeen());
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
            
            returnObj.setInvitationFormField(invitationFormFieldResponseList);
            
            return ResponseEntity.ok(returnObj);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/emptyState/{invitationId}")
    public ResponseEntity<?> emptyState(@PathVariable Long invitationId) {
        return invitationRepo.findById(invitationId).map(detail -> {
            
            List<InvitationFormField> invitationFormField = invitationFormFieldRepository.findByInvitationTypeId(detail.getId());
            
            PublicInvitationDetailResponse returnObj = new PublicInvitationDetailResponse();
            returnObj.setInvitationId(detail.getId());
            returnObj.setInviteeId(null);
            returnObj.setGuestCount(0);
            returnObj.setInvitationCode("");
            returnObj.setRsvpResponse(null);
            
            returnObj.setId(null);
            returnObj.setSeen(true);
            returnObj.setDietaryType(null);
            returnObj.setDietaryTypeNote("");
            returnObj.setAccessibility("");
            returnObj.setNotes("");
            
            returnObj.setArrivalDate(null);
            returnObj.setEmail("");
            returnObj.setContact("");
            returnObj.setOrganization("");
            returnObj.setSessionSelection("");
            returnObj.setTransportation("");
            returnObj.setSpecialRequests("");
            returnObj.setVipSeating("");
            returnObj.setRole("");
            returnObj.setPerformanceRole("");
            returnObj.setAvailability("");
            returnObj.setMediaOutlet("");
            returnObj.setPressId("");
            returnObj.setEntourageRole("");
            returnObj.setCompany("");
            returnObj.setDesignation("");
            
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
            
            returnObj.setInvitationFormField(invitationFormFieldResponseList);
            
            return ResponseEntity.ok(returnObj);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitForm(
            @PathVariable Long id,
            @RequestBody PublicInvitationDetailRequest submission) {
        return repo.findById(id).map(existing -> {
        	
        	Optional<RSVPResponseType> rSVPResponseTypeOpt = rSVPResponseTypeRepository.findByName(submission.getDietaryType());
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
            existing.setDietaryType(dietaryType);
            existing.setDietaryTypeNote(submission.getDietaryTypeNote());
            existing.setAccessibility(submission.getAccessibility());
            existing.setNotes(submission.getNotes());
            
            existing.setArrivalDate(submission.getArrivalDate());
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
            
            PublicInvitationDetail saved = repo.save(existing);

            InvitationNotification notif = new InvitationNotification();
            notif.setInvitation(saved.getInvitation());
            notif.setPublicInvitationDetail(saved);
            notif.setMessage("Invitee " + saved.getInvitee().getFirstName() + " responded.");
            notifRepo.save(notif);

            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Empty-state form handler
    @PostMapping("/empty-form/{invitationId}")
    public ResponseEntity<?> handleEmptyForm(
            @PathVariable Long invitationId,
            @RequestBody Invitee inviteeRequest,
            HttpServletRequest request) {

        Optional<Invitation> invitationOpt = invitationRepo.findById(invitationId);
        if (invitationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Invitation invitation = invitationOpt.get();

        // Check if invitee exists under this invitation
        Optional<Invitee> existingInvitee = inviteeRepo
                .findByFirstNameAndLastNameAndInvitationId(
                        inviteeRequest.getFirstName(),
                        inviteeRequest.getLastName(),
                        invitation.getId()
                );

        Invitee invitee;
        PublicInvitationDetail detail;

        if (existingInvitee.isPresent()) {
            invitee = existingInvitee.get();
            detail = repo.findByInvitationIdAndInviteeId(invitation.getId(), invitee.getId())
                    .orElse(null);
            if (detail == null) {
                return ResponseEntity.status(500).body("Public invitation detail not found");
            }
        } else {
            // Create new Invitee
            invitee = new Invitee();
            invitee.setFirstName(inviteeRequest.getFirstName());
            invitee.setLastName(inviteeRequest.getLastName());
            invitee.setInvitation(invitation);
            invitee = inviteeRepo.save(invitee);
            
            detail = publicInvitationDetailService.addPublicInvitationDetailByInvitee(invitee, invitation);
        }

        ResponseEntity<?> url = publicInvitationDetailService.generatePublicInvitationURLExistingInvitee(invitee.getId(), request);
        String urlString = (String) url.getBody();

        return ResponseEntity.ok(Map.of(
                "url", urlString,
                "detail", detail
        ));
    }
    
    @GetMapping("/generatePublicURLExistingInvitee/{inviteeId}")
    public ResponseEntity<?> generatePublicURLExistingInvitee(@PathVariable Long inviteeId, HttpServletRequest request) {
        return publicInvitationDetailService.generatePublicInvitationURLExistingInvitee(inviteeId, request);
    }
    
    @GetMapping("/generatePublicURLEmptyState/{invitationId}")
    public ResponseEntity<?> generatePublicURLEmptyState(@PathVariable Long invitationId, HttpServletRequest request) {
        return publicInvitationDetailService.generatePublicInvitationURLExistingInvitee(invitationId, request);
    }
}
