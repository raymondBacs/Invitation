package com.inv.invitation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class RSVPResponseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}
