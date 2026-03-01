package com.decisions.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.decisions.exception.DecisionNotFoundException;

@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(DecisionNotFoundException.class)
    public String handleDecisionNotFound(DecisionNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/history";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequest(IllegalArgumentException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/history";
    }
}
