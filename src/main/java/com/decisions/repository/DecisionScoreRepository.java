package com.decisions.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decisions.entity.DecisionScore;

public interface DecisionScoreRepository extends JpaRepository<DecisionScore, Long> {
    List<DecisionScore> findByDecisionOptionId(Long optionId);
    void deleteByDecisionOptionId(Long optionId);
}
