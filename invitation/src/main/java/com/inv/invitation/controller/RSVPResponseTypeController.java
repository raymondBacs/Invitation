package com.inv.invitation.controller;

import com.inv.invitation.model.RSVPResponseType;
import com.inv.invitation.repository.RSVPResponseTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/rsvp-responses")
@RequiredArgsConstructor
public class RSVPResponseTypeController {
    private final RSVPResponseTypeRepository repo;

    @GetMapping
    public List<RSVPResponseType> getAll() {
        return repo.findAll();
    }
}
