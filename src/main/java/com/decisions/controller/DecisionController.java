om.decisions.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.decisions.dto.RankedOptionDTO;
import com.decisions.entity.Decision;

@Controller
public class DecisionController {
 public DecisionController() {
        
    }@GetMapping("/")
    public String showDecisionForm(Model model) {
        
        return "decision-form";
    }
 @PostMapping("/decision/create")
    public String createDecision(@ModelAttribute Decision decision) {
        return "new desicion";
    }
    
    @GetMapping("/decision/{id}/detail")
    public String showDetailPage(@PathVariable Long id, Model model) {
        
        return "decision-detail";
    }

    @PostMapping("/decision/{id}/addOption")
    public String addOption(@PathVariable Long id,
                            @RequestParam String optionName) {
        return "add new option";
    }

    @PostMapping("/decision/{id}/deleteOption")
    public String deleteOption(@PathVariable Long id,
                               @RequestParam Long optionId) {
        
        return "delete option";
    }

    @PostMapping("/decision/{id}/addCriteria")
    public String addCriteria(@PathVariable Long id,
                              @RequestParam String criteriaName,
                              @RequestParam int weight) {
        
        return "add criteria";
    }

    @PostMapping("/decision/{id}/deleteCriteria")
    public String deleteCriteria(@PathVariable Long id,
                                 @RequestParam Long criteriaId) {
        
        return "delete criteria";
    }


    @GetMapping("/decision/{id}/score")
    public String showScoreForm(@PathVariable Long id, Model model) {
        
        return "score-form";
    }

    @PostMapping("/decision/{id}/score")
    public String saveScores(@PathVariable Long id,
                             @RequestParam List<Long> optionIds,
                             @RequestParam List<Long> criteriaIds,
                             @RequestParam List<Integer> scores) {
    	
       

        return "save score";
    }

    @GetMapping("/decision/{id}/result")
    public String showResult(@PathVariable Long id, Model model) {
        
        return "result";
    }


    @GetMapping("/new")
    public String startNew() {
        return "redirect:/";
    }
}

