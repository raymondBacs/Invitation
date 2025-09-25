package com.inv.invitation.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inv.invitation.model.JwtToken;
import com.inv.invitation.model.User;

import jakarta.transaction.Transactional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    Optional<JwtToken> findByToken(String token);
    List<JwtToken> findAllByUser(User user);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM JwtToken t WHERE t.expiresAt < :now")
    int deleteByExpiresAtBefore(@Param("now") LocalDateTime now);
    
    @Transactional
    @Modifying
    void deleteAllByUser(User user);
}
