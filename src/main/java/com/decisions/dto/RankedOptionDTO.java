package com.decisions.dto;

import java.util.List;

public class RankedOptionDTO {

    private int rank;
    private String optionName;
    private double totalScore;
    private double maxPossibleScore;
    private double percentage;
    private List<String> explanationLines;
    private String summary;

    public RankedOptionDTO() {}

    public RankedOptionDTO(int rank, String optionName, double totalScore,
                           double maxPossibleScore, List<String> explanationLines, String summary) {
        this.rank = rank;
        this.optionName = optionName;
        this.totalScore = totalScore;
        this.maxPossibleScore = maxPossibleScore;
        this.percentage = maxPossibleScore > 0 ? (totalScore / maxPossibleScore) * 100 : 0;
        this.explanationLines = explanationLines;
        this.summary = summary;
    }

    // Getters & Setters
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getOptionName() { return optionName; }
    public void setOptionName(String optionName) { this.optionName = optionName; }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    public double getMaxPossibleScore() { return maxPossibleScore; }
    public void setMaxPossibleScore(double maxPossibleScore) { this.maxPossibleScore = maxPossibleScore; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public List<String> getExplanationLines() { return explanationLines; }
    public void setExplanationLines(List<String> explanationLines) { this.explanationLines = explanationLines; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
