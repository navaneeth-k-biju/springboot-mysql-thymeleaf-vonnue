package com.decisions.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.decisions.entity.AppUser;
import com.decisions.entity.Criteria;
import com.decisions.entity.Decision;
import com.decisions.entity.DecisionOption;
import com.decisions.entity.DecisionScore;
import com.decisions.exception.DecisionNotFoundException;
import com.decisions.repository.AppUserRepository;
import com.decisions.repository.CriteriaRepository;
import com.decisions.repository.DecisionOptionRepository;
import com.decisions.repository.DecisionRepository;
import com.decisions.repository.DecisionScoreRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DecisionService {

    private final AppUserRepository appUserRepository;
    private final DecisionRepository decisionRepository;
    private final DecisionOptionRepository optionRepository;
    private final CriteriaRepository criteriaRepository;
    private final DecisionScoreRepository scoreRepository;

    public DecisionService(AppUserRepository appUserRepository,
                           DecisionRepository decisionRepository,
                           DecisionOptionRepository optionRepository,
                           CriteriaRepository criteriaRepository,
                           DecisionScoreRepository scoreRepository) {
        this.appUserRepository = appUserRepository;
        this.decisionRepository = decisionRepository;
        this.optionRepository = optionRepository;
        this.criteriaRepository = criteriaRepository;
        this.scoreRepository = scoreRepository;
    }

    public Decision saveDecision(Decision decision, String username) {
        decision.setOwner(getUserByUsername(username));
        return decisionRepository.save(decision);
    }

    public Decision getDecisionByIdForUser(Long id, String username) {
        Long userId = getUserByUsername(username).getId();
        return decisionRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new DecisionNotFoundException(id));
    }

    public List<Decision> getDecisionHistory(String username) {
        Long userId = getUserByUsername(username).getId();
        return decisionRepository.findByOwnerIdOrderByIdDesc(userId);
    }

    public DecisionOption addOption(Long decisionId, String optionName, String username) {
        Decision decision = getDecisionByIdForUser(decisionId, username);
        DecisionOption option = new DecisionOption(optionName, decision);
        return optionRepository.save(option);
    }

    public void deleteOption(Long decisionId, Long optionId, String username) {
        Decision decision = getDecisionByIdForUser(decisionId, username);
        boolean optionInDecision = decision.getOptions().stream()
                .anyMatch(option -> option.getId().equals(optionId));
        if (!optionInDecision) {
            throw new DecisionNotFoundException(decisionId);
        }
        optionRepository.deleteById(optionId);
    }

    public List<DecisionOption> getOptionsByDecision(Long decisionId) {
        return optionRepository.findByDecisionId(decisionId);
    }

    public Criteria addCriteria(Long decisionId, String name, int weight, String username) {
        Decision decision = getDecisionByIdForUser(decisionId, username);
        Criteria criteria = new Criteria(name, weight, decision);
        return criteriaRepository.save(criteria);
    }

    public void deleteCriteria(Long decisionId, Long criteriaId, String username) {
        Decision decision = getDecisionByIdForUser(decisionId, username);
        boolean criteriaInDecision = decision.getCriteriaList().stream()
                .anyMatch(criteria -> criteria.getId().equals(criteriaId));
        if (!criteriaInDecision) {
            throw new DecisionNotFoundException(decisionId);
        }
        criteriaRepository.deleteById(criteriaId);
    }

    public List<Criteria> getCriteriaByDecision(Long decisionId) {
        return criteriaRepository.findByDecisionId(decisionId);
    }

    public void saveScores(Long decisionId,
                           Long optionId,
                           List<Long> criteriaIds,
                           List<Integer> scoreValues,
                           String username) {
        Decision decision = getDecisionByIdForUser(decisionId, username);

        DecisionOption option = decision.getOptions().stream()
                .filter(o -> o.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Option not found for this decision."));

        Map<Long, Criteria> criteriaById = decision.getCriteriaList().stream()
                .collect(Collectors.toMap(Criteria::getId, Function.identity()));

        scoreRepository.deleteByDecisionOptionId(optionId);

        for (int i = 0; i < criteriaIds.size(); i++) {
            Criteria criteria = criteriaById.get(criteriaIds.get(i));
            if (criteria == null) {
                throw new IllegalArgumentException("Criteria not found for this decision.");
            }
            DecisionScore score = new DecisionScore(scoreValues.get(i), option, criteria);
            scoreRepository.save(score);
        }
    }

    public List<DecisionScore> getScoresByOption(Long optionId) {
        return scoreRepository.findByDecisionOptionId(optionId);
    }

    private AppUser getUserByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }
}

