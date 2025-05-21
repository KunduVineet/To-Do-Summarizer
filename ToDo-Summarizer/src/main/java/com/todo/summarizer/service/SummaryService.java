package com.todo.summarizer.service;

import com.todo.summarizer.model.Todo;
import com.todo.summarizer.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SummaryService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    public RestTemplate restTemplate;

    @Value("${llm.api.key}")
    private String llmApiKey;

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    public String summarizeAndSendToSlack() {
        //Fetch pending To-dos
        List<Todo> pendingTodos = todoRepository.findByStatus("pending");
        if(pendingTodos.isEmpty()){
            return "No pending To-dos to summarize";
        }

        //Format To-dos for LLM
        String prompt = "Summarize the following To-do-list: \n"+
                pendingTodos.stream()
                        .map(todo -> todo.getTitle() + (todo.getDescription() != null ? " - " + todo.getDescription() : ""))
                        .collect(Collectors.joining(", "));
        return prompt;
    }
}
