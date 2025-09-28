package com.inv.invitation.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class GiftPreference {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime modified;

	@ManyToOne(optional = false)
    private Invitation invitation;
	
    // Type of preference: e.g., "CASH", "VOUCHER", "SPECIFIC_ITEM", "OTHER"
    @Column(columnDefinition = "TEXT", nullable = false)
    private String giftType;

    // Description of the preference (e.g., "We prefer cash gifts", "Travel fund contributions")
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    // Optional link if the preference points to an online platform or store
    @Column(length = 1000)
    private String link;

    // Priority (to rank preferences if multiple exist)
    private Integer priority;

    // Soft delete / enable-disable
    private boolean deleted = false;
}
