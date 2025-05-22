package com.todo.summarizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.summarizer.model.Todo;
import com.todo.summarizer.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SummaryService {

    private static final Logger logger = LoggerFactory.getLogger(SummaryService.class);

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${llm.api.key}")
    private String llmApiKey;

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    @Value("${llm.api.url:https://api.cohere.ai/v1/generate}")
    private String llmUrl;

    public String summarizeAndSendToSlack() throws Exception {
        logger.info("Fetching pending todos...");
        List<Todo> pendingTodos = todoRepository.findByStatus("pending");
        if (pendingTodos.isEmpty()) {
            logger.info("No pending todos found.");
            return "No pending to-dos to summarize.";
        }

        // Format prompt, replacing newlines and special characters
        String prompt = "Summarize the following to-do list: " +
                pendingTodos.stream()
                        .map(todo -> {
                            String title = todo.getTitle().replaceAll("[\\n\\r\\t]", " ").trim();
                            String description = todo.getDescription() != null ? todo.getDescription().replaceAll("[\\n\\r\\t]", " ").trim() : "";
                            return title + (description.isEmpty() ? "" : " - " + description);
                        })
                        .collect(Collectors.joining(", "));
        logger.info("Generated prompt: {}", prompt);

        // Create Cohere request
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + llmApiKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> llmRequestMap = new HashMap<>();
        llmRequestMap.put("prompt", prompt);
        llmRequestMap.put("max_tokens", 100);
        llmRequestMap.put("temperature", 0.7); // Optional: Control randomness
        String llmRequest;
        try {
            llmRequest = objectMapper.writeValueAsString(llmRequestMap);
            logger.info("Cohere request payload: {}", llmRequest);
        } catch (Exception e) {
            logger.error("Failed to serialize Cohere request: {}", e.getMessage());
            throw new RuntimeException("Failed to create Cohere request: " + e.getMessage());
        }

        // Call Cohere API
        String summary;
        try {
            HttpEntity<String> llmEntity = new HttpEntity<>(llmRequest, headers);
            ResponseEntity<String> response = restTemplate.exchange(llmUrl, HttpMethod.POST, llmEntity, String.class);
            logger.info("Cohere response: {}", response.getBody());
            summary = extractSummaryFromResponse(response.getBody());
        } catch (HttpClientErrorException e) {
            logger.error("Cohere API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 429) {
                logger.info("Rate limit hit, retrying after 1 second...");
                Thread.sleep(1000);
                HttpEntity<String> llmEntity = new HttpEntity<>(llmRequest, headers);
                ResponseEntity<String> retryResponse = restTemplate.exchange(llmUrl, HttpMethod.POST, llmEntity, String.class);
                summary = extractSummaryFromResponse(retryResponse.getBody());
            } else if (e.getStatusCode().value() == 401) {
                throw new RuntimeException("Invalid LLM API key");
            } else {
                throw new RuntimeException("Cohere API error: " + e.getResponseBodyAsString());
            }
        }

        // Post to Slack
        String slackPayload = objectMapper.writeValueAsString(Map.of("text", summary));
        logger.info("Slack payload: {}", slackPayload);
        try {
            restTemplate.postForObject(slackWebhookUrl, slackPayload, String.class);
            logger.info("Successfully posted to Slack.");
        } catch (HttpClientErrorException e) {
            logger.error("Slack API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Slack API error: " + e.getResponseBodyAsString());
        }

        return "Summary posted to Slack successfully.";
    }

    private String extractSummaryFromResponse(String response) throws Exception {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            List<Map<String, String>> generations = (List<Map<String, String>>) responseMap.get("generations");
            if (generations == null || generations.isEmpty()) {
                throw new RuntimeException("No generations found in Cohere response");
            }
            String summary = generations.get(0).get("text").trim();
            logger.info("Extracted summary: {}", summary);
            return summary;
        } catch (Exception e) {
            logger.error("Failed to parse Cohere response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse Cohere response: " + e.getMessage());
        }
    }
}