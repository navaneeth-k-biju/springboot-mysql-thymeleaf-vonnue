package com.decisions.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.decisions.entity.Criteria;
import com.decisions.entity.Decision;
import com.decisions.entity.DecisionOption;
import com.decisions.entity.DecisionScore;
import com.decisions.repository.CriteriaRepository;
import com.decisions.repository.DecisionOptionRepository;
import com.decisions.repository.DecisionRepository;
import com.decisions.repository.DecisionScoreRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DecisionService {

    private final DecisionRepository decisionRepository;
    private final DecisionOptionRepository optionRepository;
    private final CriteriaRepository criteriaRepository;
    private final DecisionScoreRepository scoreRepository;

    public DecisionService(DecisionRepository decisionRepository,
                           DecisionOptionRepository optionRepository,
                           CriteriaRepository criteriaRepository,
                           DecisionScoreRepository scoreRepository) {
        this.decisionRepository = decisionRepository;
        this.optionRepository = optionRepository;
        this.criteriaRepository = criteriaRepository;
        this.scoreRepository = scoreRepository;
    }

    public Decision saveDecision(Decision decision) {
        return decisionRepository.save(decision);
    }

    public Decision getDecisionById(Long id) {
        return decisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Decision not found with id: " + id));
    }

    public DecisionOption addOption(Long decisionId, String optionName) {
        Decision decision = getDecisionById(decisionId);
        DecisionOption option = new DecisionOption(optionName, decision);
        return optionRepository.save(option);
    }

    public void deleteOption(Long optionId) {
        optionRepository.deleteById(optionId);
    }

    public List<DecisionOption> getOptionsByDecision(Long decisionId) {
        return optionRepository.findByDecisionId(decisionId);
    }

    public Criteria addCriteria(Long decisionId, String name, int weight) {
        Decision decision = getDecisionById(decisionId);
        Criteria criteria = new Criteria(name, weight, decision);
        return criteriaRepository.save(criteria);
    }

    public void deleteCriteria(Long criteriaId) {
        criteriaRepository.deleteById(criteriaId);
    }

    public List<Criteria> getCriteriaByDecision(Long decisionId) {
        return criteriaRepository.findByDecisionId(decisionId);
    }

    public void saveScores(Long optionId, List<Long> criteriaIds, List<Integer> scoreValues) {
        DecisionOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        scoreRepository.deleteByDecisionOptionId(optionId);

        for (int i = 0; i < criteriaIds.size(); i++) {
            Criteria criteria = criteriaRepository.findById(criteriaIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Criteria not found"));
            DecisionScore score = new DecisionScore(scoreValues.get(i), option, criteria);
            scoreRepository.save(score);
        }
    }

    public List<DecisionScore> getScoresByOption(Long optionId) {
        return scoreRepository.findByDecisionOptionId(optionId);
    }
}

