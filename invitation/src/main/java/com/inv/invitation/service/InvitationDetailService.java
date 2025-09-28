package com.inv.invitation.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inv.invitation.dto.InvitationDetailRequest;
import com.inv.invitation.model.Invitation;
import com.inv.invitation.model.InvitationDetail;
import com.inv.invitation.repository.InvitationDetailRepository;
import com.inv.invitation.repository.InvitationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvitationDetailService {

    private final InvitationDetailRepository detailRepository;
    private final InvitationRepository invitationRepository;
    
    public Optional<InvitationDetail> getInvitationDetailByInvitationId(Long invitationId) {
    	return detailRepository.findByInvitationid(invitationId);
    }
    
    public InvitationDetail saved(InvitationDetail detail) {
    	return detailRepository.save(detail);
    }

    @Transactional
    public InvitationDetail create(InvitationDetailRequest request) {
        Invitation invitation = invitationRepository.findById(request.getInvitationId())
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

        InvitationDetail detail = new InvitationDetail();
        detail.setInvitation(invitation);
        detail.setEventTitle(request.getEventTitle());
        detail.setIntroduction(request.getIntroduction());
        detail.setEvent_date(request.getEventDate());
        detail.setDateLocked(request.getDateLocked());
        detail.setLockedDaysBeforeEvent(request.getLockedDaysBeforeEvent());
        detail.setDressCode(request.getDressCode());
        detail.setTheme(request.getTheme());
        detail.setEventStartTime(request.getEventStartTime());
        detail.setEventEndTime(request.getEventEndTime());
        detail.setHasGiftPreference(request.getHasGiftPreference());
        detail.setSpecialInstructions(request.getSpecialInstructions());

        return detailRepository.save(detail);
    }

    @Transactional
    public InvitationDetail update(Long id, InvitationDetailRequest request) {
        InvitationDetail detail = detailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("InvitationDetail not found"));

        Optional.ofNullable(request.getEventTitle()).ifPresent(detail::setEventTitle);
        Optional.ofNullable(request.getIntroduction()).ifPresent(detail::setIntroduction);
        Optional.ofNullable(request.getEventDate()).ifPresent(detail::setEvent_date);
        Optional.ofNullable(request.getDateLocked()).ifPresent(detail::setDateLocked);
        Optional.ofNullable(request.getLockedDaysBeforeEvent()).ifPresent(detail::setLockedDaysBeforeEvent);
        Optional.ofNullable(request.getDressCode()).ifPresent(detail::setDressCode);
        Optional.ofNullable(request.getTheme()).ifPresent(detail::setTheme);
        Optional.ofNullable(request.getEventStartTime()).ifPresent(detail::setEventStartTime);
        Optional.ofNullable(request.getEventEndTime()).ifPresent(detail::setEventEndTime);
        Optional.ofNullable(request.getHasGiftPreference()).ifPresent(detail::setHasGiftPreference);
        Optional.ofNullable(request.getSpecialInstructions()).ifPresent(detail::setSpecialInstructions);

        return detailRepository.save(detail);
    }
}