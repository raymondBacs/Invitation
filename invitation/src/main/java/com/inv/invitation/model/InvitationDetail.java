package com.inv.invitation.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class InvitationDetail {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime modified;
	
	@OneToOne
	private Invitation invitation;
	
	private String eventTitle;
	
	@Column(columnDefinition = "TEXT")
    private String introduction;
    
    @Column(name = "event_date")
    private LocalDateTime event_date;
    
    private Boolean dateLocked = false;
    
    private Integer lockedDaysBeforeEvent;
    
    // Event-Specific Extras (Highly Recommended)
    private String dressCode;
    private String theme;
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;
    // Program Flow | Object list different MODEL
    // Gift Preferences | Object list different MODEL
    private Boolean hasGiftPreference;
    private String specialInstructions;
}
