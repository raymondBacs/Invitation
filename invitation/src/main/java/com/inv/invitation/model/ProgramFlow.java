package com.inv.invitation.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class ProgramFlow {
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
    
    private Integer sequenceOrder;
    
    private LocalDateTime programTime;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Long durationSeconds; // store everything in seconds
}
