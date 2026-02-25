package com.decisions.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.decisions.dto.RankedOptionDTO;
import com.decisions.entity.Criteria;
import com.decisions.entity.Decision;
import com.decisions.entity.DecisionOption;
import com.decisions.entity.DecisionScore;
import com.decisions.repository.DecisionScoreRepository;

@Service
public class EvaluationService {

    private final DecisionScoreRepository scoreRepository;

    public EvaluationService(DecisionScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public List<RankedOptionDTO> evaluate(Decision decision) {
        List<DecisionOption> options = decision.getOptions();
        List<Criteria> criteriaList = decision.getCriteriaList();

        double maxPossibleScore = criteriaList.stream()
                .mapToDouble(c -> c.getWeight() * 10.0)
                .sum();

        List<RankedOptionDTO> results = new ArrayList<>();

        for (DecisionOption option : options) {
            List<DecisionScore> scores = scoreRepository.findByDecisionOptionId(option.getId());

            Map<Long, Integer> scoreMap = scores.stream()
                    .collect(Collectors.toMap(
                            s -> s.getCriteria().getId(),
                            DecisionScore::getScore
                    ));

            double totalScore = 0;
            List<String> explanationLines = new ArrayList<>();

            for (Criteria criteria : criteriaList) {
                int userScore = scoreMap.getOrDefault(criteria.getId(), 0);
                double weightedScore = criteria.getWeight() * userScore;
                totalScore += weightedScore;

                String importance = importanceLabel(criteria.getWeight());
                explanationLines.add(String.format(
                        "%s — Score: %d/10 | Weight: %d (%s) | Contribution: %.1f pts",
                        criteria.getName(), userScore, criteria.getWeight(), importance, weightedScore
                ));
            }

            results.add(new RankedOptionDTO(0, option.getName(), totalScore,
                    maxPossibleScore, explanationLines, ""));
        }

        results.sort((a, b) -> Double.compare(b.getTotalScore(), a.getTotalScore()));

        for (int i = 0; i < results.size(); i++) {
            RankedOptionDTO dto = results.get(i);
            dto.setRank(i + 1);
            dto.setSummary(buildSummary(dto, i, results, criteriaList));
        }

        return results;
    }

    private String buildSummary(RankedOptionDTO dto, int index,
                                List<RankedOptionDTO> allResults, List<Criteria> criteriaList) {
        if (index == 0) {
            String topCriteria = criteriaList.stream()
                    .sorted((a, b) -> Integer.compare(b.getWeight(), a.getWeight()))
                    .findFirst()
                    .map(Criteria::getName)
                    .orElse("key criteria");

            return String.format(
                    "\"%s\" is the top recommendation with %.1f%% overall satisfaction. " +
                    "It performed best when considering heavily weighted criteria like \"%s\".",
                    dto.getOptionName(), dto.getPercentage(), topCriteria
            );
        } else {
            RankedOptionDTO best = allResults.get(0);
            double gap = best.getTotalScore() - dto.getTotalScore();
            return String.format(
                    "\"%s\" ranked #%d, scoring %.1f%% overall — %.1f points behind \"%s\".",
                    dto.getOptionName(), dto.getRank(), dto.getPercentage(), gap, best.getOptionName()
            );
        }
    }

    private String importanceLabel(int weight) {
        return switch (weight) {
            case 1 -> "Very Low";
            case 2 -> "Low";
            case 3 -> "Medium";
            case 4 -> "High";
            case 5 -> "Very High";
            default -> "Unknown";
        };
    }
}
