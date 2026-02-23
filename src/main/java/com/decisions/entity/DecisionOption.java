package com.decisions.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "decision_options")
public class DecisionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "decision_id", nullable = false)
    private Decision decision;

    @OneToMany(mappedBy = "decisionOption", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DecisionScore> scores = new ArrayList<>();

    public DecisionOption() {}

    public DecisionOption(String name, Decision decision) {
        this.name = name;
        this.decision = decision;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Decision getDecision() { return decision; }
    public void setDecision(Decision decision) { this.decision = decision; }

    public List<DecisionScore> getScores() { return scores; }
    public void setScores(List<DecisionScore> scores) { this.scores = scores; }
}
