package com.decisions.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decisions.entity.Decision;

public interface DecisionRepository extends JpaRepository<Decision, Long> {
    List<Decision> findAllByOrderByIdDesc();
    List<Decision> findByOwnerIdOrderByIdDesc(Long ownerId);
    Optional<Decision> findByIdAndOwnerId(Long id, Long ownerId);
}
