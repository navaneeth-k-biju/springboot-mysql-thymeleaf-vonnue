package com.decisions.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.decisions.dto.RankedOptionDTO;
import com.decisions.entity.Decision;
import com.decisions.service.DecisionService;
import com.decisions.service.EvaluationService;

@Controller
public class DecisionController {

    private final DecisionService decisionService;
    private final EvaluationService evaluationService;

    public DecisionController(DecisionService decisionService, EvaluationService evaluationService) {
        this.decisionService = decisionService;
        this.evaluationService = evaluationService;
    }

    @GetMapping("/")
    public String showDecisionForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("decision", new Decision());
        model.addAttribute("username", userDetails.getUsername());
        return "decision-form";
    }

    @GetMapping("/history")
    public String showHistory(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("decisions", decisionService.getDecisionHistory(userDetails.getUsername()));
        model.addAttribute("username", userDetails.getUsername());
        return "history";
    }

    @PostMapping("/decision/create")
    public String createDecision(@ModelAttribute Decision decision,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        Decision saved = decisionService.saveDecision(decision, userDetails.getUsername());
        return "redirect:/decision/" + saved.getId() + "/detail";
    }
    
    @GetMapping("/decision/{id}/detail")
    public String showDetailPage(@PathVariable Long id,
                                 Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        Decision decision = decisionService.getDecisionByIdForUser(id, userDetails.getUsername());
        model.addAttribute("decision", decision);
        model.addAttribute("username", userDetails.getUsername());
        return "decision-detail";
    }

    @PostMapping("/decision/{id}/addOption")
    public String addOption(@PathVariable Long id,
                            @RequestParam String optionName,
                            @AuthenticationPrincipal UserDetails userDetails) {
        decisionService.addOption(id, optionName, userDetails.getUsername());
        return "redirect:/decision/" + id + "/detail";
    }

    @PostMapping("/decision/{id}/deleteOption")
    public String deleteOption(@PathVariable Long id,
                               @RequestParam Long optionId,
                               @AuthenticationPrincipal UserDetails userDetails) {
        decisionService.deleteOption(id, optionId, userDetails.getUsername());
        return "redirect:/decision/" + id + "/detail";
    }

    @PostMapping("/decision/{id}/addCriteria")
    public String addCriteria(@PathVariable Long id,
                              @RequestParam String criteriaName,
                              @RequestParam int weight,
                              @AuthenticationPrincipal UserDetails userDetails) {
        decisionService.addCriteria(id, criteriaName, weight, userDetails.getUsername());
        return "redirect:/decision/" + id + "/detail";
    }

    @PostMapping("/decision/{id}/deleteCriteria")
    public String deleteCriteria(@PathVariable Long id,
                                 @RequestParam Long criteriaId,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        decisionService.deleteCriteria(id, criteriaId, userDetails.getUsername());
        return "redirect:/decision/" + id + "/detail";
    }


    @GetMapping("/decision/{id}/score")
    public String showScoreForm(@PathVariable Long id,
                                Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        Decision decision = decisionService.getDecisionByIdForUser(id, userDetails.getUsername());

        if (decision.getOptions().isEmpty() || decision.getCriteriaList().isEmpty()) {
            model.addAttribute("decision", decision);
            model.addAttribute("error", "Please add at least one option and one criterion before scoring.");
            model.addAttribute("username", userDetails.getUsername());
            return "decision-detail";
        }

        model.addAttribute("decision", decision);
        model.addAttribute("username", userDetails.getUsername());
        return "score-form";
    }

    @PostMapping("/decision/{id}/score")
    public String saveScores(@PathVariable Long id,
                             @RequestParam List<Long> optionIds,
                             @RequestParam List<Long> criteriaIds,
                             @RequestParam List<Integer> scores,
                             @AuthenticationPrincipal UserDetails userDetails) {
    	
        int criteriaCount = criteriaIds.size();

        for (int i = 0; i < optionIds.size(); i++) {
            List<Integer> optionScores = scores.subList(i * criteriaCount, (i + 1) * criteriaCount);
            decisionService.saveScores(id, optionIds.get(i), criteriaIds, optionScores, userDetails.getUsername());
        }

        return "redirect:/decision/" + id + "/result";
    }

    @GetMapping("/decision/{id}/result")
    public String showResult(@PathVariable Long id,
                             Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {
        Decision decision = decisionService.getDecisionByIdForUser(id, userDetails.getUsername());
        List<RankedOptionDTO> rankedOptions = evaluationService.evaluate(decision);

        model.addAttribute("decision", decision);
        model.addAttribute("rankedOptions", rankedOptions);
        model.addAttribute("topOption", rankedOptions.isEmpty() ? null : rankedOptions.get(0));
        model.addAttribute("username", userDetails.getUsername());
        return "result";
    }


    @GetMapping("/new")
    public String startNew() {
        return "redirect:/";
    }
}

