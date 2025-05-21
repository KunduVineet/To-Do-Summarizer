package com.todo.summarizer.controller;

import com.todo.summarizer.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/todos/summary")
@RestController
public class SummaryController {

    @Autowired
    private SummaryService summaryService;

    @PostMapping
    public ResponseEntity<String> summarizeTodos(){
        try{
            String result = summaryService.summarizeAndSendToSlack();
            return ResponseEntity.ok(result);
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Failed to Summarize "+e.getMessage());
        }
    }
}
