package com.decisions.controller;

import java.time.Duration;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.decisions.dto.LoginRequest;
import com.decisions.dto.RegisterRequest;
import com.decisions.security.JwtAuthenticationFilter;
import com.decisions.security.JwtService;
import com.decisions.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            String token = jwtService.generateToken(authentication.getName());
            response.addHeader("Set-Cookie",
                    org.springframework.http.ResponseCookie.from(JwtAuthenticationFilter.AUTH_COOKIE, token)
                            .httpOnly(true)
                            .path("/")
                            .maxAge(Duration.ofDays(7))
                            .sameSite("Lax")
                            .build()
                            .toString());
            return "redirect:/";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password.");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest registerRequest,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.register(registerRequest);
            redirectAttributes.addFlashAttribute("success", "Registration successful. Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        response.addHeader("Set-Cookie",
                org.springframework.http.ResponseCookie.from(JwtAuthenticationFilter.AUTH_COOKIE, "")
                        .httpOnly(true)
                        .path("/")
                        .maxAge(0)
                        .sameSite("Lax")
                        .build()
                        .toString());
        return "redirect:/login";
    }
}
