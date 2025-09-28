package com.inv.invitation.service;

import com.inv.invitation.dto.GiftPreferenceResponse;
import com.inv.invitation.model.GiftPreference;
import com.inv.invitation.model.Invitation;
import com.inv.invitation.repository.GiftPreferenceRepository;
import com.inv.invitation.repository.InvitationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GiftPreferenceService {

    private final GiftPreferenceRepository giftPreferenceRepository;
    private final InvitationRepository invitationRepository;

    public GiftPreferenceService(GiftPreferenceRepository giftPreferenceRepository,
                                 InvitationRepository invitationRepository) {
        this.giftPreferenceRepository = giftPreferenceRepository;
        this.invitationRepository = invitationRepository;
    }
    

    public GiftPreference createGiftPreference(GiftPreference giftPreference) {
        return giftPreferenceRepository.save(giftPreference);
    }

    public GiftPreference updateGiftPreference(Long id, GiftPreferenceResponse updated) {
        GiftPreference existing = giftPreferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GiftPreference not found"));

        existing.setGiftType(updated.getGiftType());
        existing.setDescription(updated.getDescription());
        existing.setLink(updated.getLink());
        existing.setPriority(updated.getPriority());
        return giftPreferenceRepository.save(existing);
    }
    
    public List<GiftPreferenceResponse> getGiftPreferencesResponseList(List<GiftPreference> giftPreferenceList) {
    	List<GiftPreferenceResponse> giftPreferenceResponseList = new ArrayList<GiftPreferenceResponse>();
        for(GiftPreference currentGiftPreference : giftPreferenceList) {
        	GiftPreferenceResponse giftPreferenceResponseObject = new GiftPreferenceResponse();
        	
        	giftPreferenceResponseObject.setInvitationId(currentGiftPreference.getInvitation().getId());
        	giftPreferenceResponseObject.setGiftType(currentGiftPreference.getGiftType());
        	giftPreferenceResponseObject.setDescription(currentGiftPreference.getDescription());
        	giftPreferenceResponseObject.setLink(currentGiftPreference.getLink());
        	giftPreferenceResponseObject.setPriority(currentGiftPreference.getPriority());
        	
        	giftPreferenceResponseList.add(giftPreferenceResponseObject);
        }
        return giftPreferenceResponseList;
    }

    public List<GiftPreference> getGiftPreferencesByInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));
        return giftPreferenceRepository.findByInvitationAndDeletedFalse(invitation);
    }

    public void deleteGiftPreference(Long id) {
        GiftPreference giftPreference = giftPreferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GiftPreference not found"));
        giftPreference.setDeleted(true);
        giftPreferenceRepository.save(giftPreference);
    }
}
