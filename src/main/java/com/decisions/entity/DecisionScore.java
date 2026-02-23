package com.decisions.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "decision_scores")
public class DecisionScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int score;

    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private DecisionOption decisionOption;

    @ManyToOne
    @JoinColumn(name = "criteria_id", nullable = false)
    private Criteria criteria;

    public DecisionScore() {}

    public DecisionScore(int score, DecisionOption decisionOption, Criteria criteria) {
        this.score = score;
        this.decisionOption = decisionOption;
        this.criteria = criteria;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public DecisionOption getDecisionOption() { return decisionOption; }
    public void setDecisionOption(DecisionOption decisionOption) { this.decisionOption = decisionOption; }

    public Criteria getCriteria() { return criteria; }
    public void setCriteria(Criteria criteria) { this.criteria = criteria; }
}
