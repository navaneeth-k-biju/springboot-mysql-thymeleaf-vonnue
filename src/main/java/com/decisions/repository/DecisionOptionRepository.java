package com.decisions.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decisions.entity.DecisionOption;

public interface DecisionOptionRepository extends JpaRepository<DecisionOption, Long> {
    List<DecisionOption> findByDecisionId(Long decisionId);
}
