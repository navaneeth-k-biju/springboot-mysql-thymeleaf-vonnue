package com.decisions.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decisions.entity.Criteria;

public interface CriteriaRepository extends JpaRepository<Criteria, Long> {
    List<Criteria> findByDecisionId(Long decisionId);
}
