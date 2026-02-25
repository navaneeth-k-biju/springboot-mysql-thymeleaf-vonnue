package com.decisions.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decisions.entity.Decision;

public interface DecisionRepository extends JpaRepository<Decision, Long> {
}
