package com.inv.invitation.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inv.invitation.model.DietaryType;
import com.inv.invitation.model.InvitationType;
import com.inv.invitation.model.InviteeType;
import com.inv.invitation.model.RSVPResponseType;
import com.inv.invitation.model.Role;
import com.inv.invitation.repository.DietaryTypeRepository;
import com.inv.invitation.repository.InvitationTypeRepository;
import com.inv.invitation.repository.InviteeTypeRepository;
import com.inv.invitation.repository.RSVPResponseTypeRepository;
import com.inv.invitation.repository.RoleRepository;

@Configuration
public class DataInitializer {

    @Bean
    ApplicationRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            roleRepository.findByName("ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setName("ADMIN");
                role.setDescription("System administrator with full access");
                role.setCreatedBy("system");
                return roleRepository.save(role);
            });

            roleRepository.findByName("INVITER").orElseGet(() -> {
                Role role = new Role();
                role.setName("INVITER");
                role.setDescription("User who can send invitations");
                role.setCreatedBy("system");
                return roleRepository.save(role);
            });
        };
    }
    
    @Bean
    ApplicationRunner initInvitationTypes(InvitationTypeRepository invitationTypeRepository) {
        return args -> {
            String[] defaults = {
                    "Wedding", "Birthday", "Debut (18th Birthday)", "Baptism / Christening", "Graduation", "Housewarming",
                    "Anniversary", "Engagement Party", "Bridal Shower", "Baby Shower", "Gender Reveal", "Farewell Party",
                    "Retirement Party", "Reunion", "Corporate Event", "Conference / Seminar", "Workshop / Training",
                    "Product Launch", "Fundraising / Charity Event", "Holiday Party", "Festival / Community Event",
                    "Awarding / Recognition Ceremony", "Memorial / Remembrance", "Cultural / Religious Event"
            };

            for (String name : defaults) {
                invitationTypeRepository.findByName(name).orElseGet(() -> {
                    InvitationType it = new InvitationType();
                    it.setName(name);
                    it.setDescription(name);
                    it.setCreatedBy("system");
                    return invitationTypeRepository.save(it);
                });
            }
        };
    }

    @Bean
    ApplicationRunner initInviteeTypes(InviteeTypeRepository inviteeTypeRepository) {
        return args -> {
            String[] defaults = {
                    "Family", "Relatives", "Close Friends", "School / College Friends", "Barkada / Peer Group",
                    "Colleagues / Workmates", "Boss / Superior", "Business Partner / Client", "Neighbors",
                    "Community / Organization Member", "Church / Religious Member", "VIP / Special Guest",
                    "Sponsor / Godparent", "Entourage", "Performer / Program Participant", "Media / Press",
                    "General Guest / Public"
            };

            for (String name : defaults) {
                inviteeTypeRepository.findByName(name).orElseGet(() -> {
                    InviteeType t = new InviteeType();
                    t.setName(name);
                    t.setDescription(name);
                    t.setCreatedBy("system");
                    return inviteeTypeRepository.save(t);
                });
            }
        };
    }
    
    @Bean
    ApplicationRunner initDietaryTypes(DietaryTypeRepository dietaryTypeRepository) {
        return args -> {
            String[] defaults = {
                    "No Preference",
                    "Vegetarian",
                    "Vegan",
                    "Pescatarian",
                    "Halal",
                    "Kosher",
                    "Gluten-Free",
                    "Lactose-Free",
                    "Nut-Free"
            };

            for (String name : defaults) {
                dietaryTypeRepository.findByName(name).orElseGet(() -> {
                    DietaryType dt = new DietaryType();
                    dt.setName(name);
                    return dietaryTypeRepository.save(dt);
                });
            }
        };
    }

    @Bean
    ApplicationRunner initRSVPResponseTypes(RSVPResponseTypeRepository rsvpResponseTypeRepository) {
        return args -> {
            String[] defaults = {
                    "Attending",
                    "Not Attending",
                    "Maybe"
            };

            for (String name : defaults) {
                rsvpResponseTypeRepository.findByName(name).orElseGet(() -> {
                    RSVPResponseType rt = new RSVPResponseType();
                    rt.setName(name);
                    return rsvpResponseTypeRepository.save(rt);
                });
            }
        };
    }
}
