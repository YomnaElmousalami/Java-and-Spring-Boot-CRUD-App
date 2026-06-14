package com.burnoutdetector.demo;

import com.burnoutdetector.demo.model.CalendarSummary;
import com.burnoutdetector.demo.service.AiService;
import com.burnoutdetector.demo.service.BurnoutAnalysisService;
import com.burnoutdetector.demo.service.CalendarService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CalendarService calendarService;
    private final BurnoutAnalysisService burnoutAnalysisService;
    private final AiService aiService;

    public HomeController(CalendarService calendarService, BurnoutAnalysisService burnoutAnalysisService, AiService aiService) {
        this.calendarService = calendarService;
        this.burnoutAnalysisService = burnoutAnalysisService;
        this.aiService = aiService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                            Model model) throws Exception {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        CalendarSummary summary = calendarService.getCalendarSummary(accessToken);
        model.addAttribute("summary", summary);
        return "dashboard";
    }

    @GetMapping("/report")
    public String report(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                         Model model) throws Exception {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        CalendarSummary summary = calendarService.getCalendarSummary(accessToken);
        int score = burnoutAnalysisService.calculateScore(summary);
        String recommendations = aiService.getRecommendations(summary, score);
        String scoreColor = score >= 70 ? "#f44336" : score >= 40 ? "#FF9800" : "#4CAF50";
        String riskLevel = score >= 70 ? "High Risk" : score >= 40 ? "Moderate Risk" : "Low Risk";
        model.addAttribute("score", score);
        model.addAttribute("scoreColor", scoreColor);
        model.addAttribute("riskLevel", riskLevel);
        model.addAttribute("recommendations", recommendations);
        return "report";
    }

}
