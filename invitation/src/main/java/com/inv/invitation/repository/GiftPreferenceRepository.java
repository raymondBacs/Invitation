package com.inv.invitation.repository;

import com.inv.invitation.model.GiftPreference;
import com.inv.invitation.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftPreferenceRepository extends JpaRepository<GiftPreference, Long> {
    List<GiftPreference> findByInvitationAndDeletedFalse(Invitation invitation);
}
