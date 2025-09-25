package com.inv.invitation.controller;

import com.inv.invitation.model.DietaryType;
import com.inv.invitation.repository.DietaryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/dietary-types")
@RequiredArgsConstructor
public class DietaryTypeController {
    private final DietaryTypeRepository repo;

    @GetMapping
    public List<DietaryType> getAll() {
        return repo.findAll();
    }
}
