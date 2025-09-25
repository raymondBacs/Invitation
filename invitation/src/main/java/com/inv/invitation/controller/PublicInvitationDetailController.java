package com.inv.invitation.controller;

import com.inv.invitation.model.*;
import com.inv.invitation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/public-invitation")
@RequiredArgsConstructor
public class PublicInvitationDetailController {
    private final PublicInvitationDetailRepository repo;
    private final InvitationNotificationRepository notifRepo;
    private final InvitationRepository invitationRepo;
    private final InviteeRepository inviteeRepo;

    @GetMapping("/{code}/{first}-{last}")
    public ResponseEntity<PublicInvitationDetail> openInvitation(
            @PathVariable String code,
            @PathVariable String first,
            @PathVariable String last) {
        return repo.findByInvitationCode(code).map(detail -> {
            detail.setSeen(true);
            repo.save(detail);
            return ResponseEntity.ok(detail);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<PublicInvitationDetail> submitForm(
            @PathVariable Long id,
            @RequestBody PublicInvitationDetail submission) {
        return repo.findById(id).map(existing -> {
            existing.setRsvpResponse(submission.getRsvpResponse());
            existing.setGuestCount(submission.getGuestCount());
            existing.setDietaryType(submission.getDietaryType());
            existing.setDietaryTypeNote(submission.getDietaryTypeNote());
            existing.setAccessibility(submission.getAccessibility());
            existing.setNotes(submission.getNotes());
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
            @RequestBody Invitee inviteeRequest) {

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

            // Create new PublicInvitationDetail
            detail = new PublicInvitationDetail();
            detail.setInvitation(invitation);
            detail.setInvitee(invitee);
            detail.setInvitationCode(generateUniqueCode());
            detail.setSeen(false);
            detail = repo.save(detail);
        }

        String url = String.format("https://{domain}/%s/%s-%s",
                detail.getInvitationCode(),
                invitee.getFirstName(),
                invitee.getLastName());

        return ResponseEntity.ok(Map.of(
                "url", url,
                "detail", detail
        ));
    }

    // ✅ helper for 8-digit unique code
    private String generateUniqueCode() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
    }
}
