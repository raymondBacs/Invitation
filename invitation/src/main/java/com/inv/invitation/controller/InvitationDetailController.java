package com.inv.invitation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inv.invitation.dto.InvitationDetailRequest;
import com.inv.invitation.model.InvitationDetail;
import com.inv.invitation.service.InvitationDetailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/invitation-details")
@RequiredArgsConstructor
public class InvitationDetailController {

    private final InvitationDetailService detailService;

    @PostMapping
    public ResponseEntity<InvitationDetail> create(@RequestBody InvitationDetailRequest request) {
        return ResponseEntity.ok(detailService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvitationDetail> update(@PathVariable Long id, @RequestBody InvitationDetailRequest request) {
        return ResponseEntity.ok(detailService.update(id, request));
    }
}