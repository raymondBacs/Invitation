package com.inv.invitation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inv.invitation.model.User;
import com.inv.invitation.model.UserDetail;

public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {
    Optional<UserDetail> findByUser(User user);
}
