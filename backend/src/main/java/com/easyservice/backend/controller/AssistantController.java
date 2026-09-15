package com.easyservice.backend.controller;

import com.easyservice.backend.service.AssistantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assistant")
@CrossOrigin(origins = "*")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AssistantService.ChatResponse> chat(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(assistantService.chat(
                request.messages() == null ? List.of() : request.messages(),
                request.inventory() == null ? List.of() : request.inventory(),
                request.language()
        ));
    }

    public record ChatRequest(
            List<Map<String, Object>> messages,
            List<Map<String, Object>> inventory,
            String language
    ) {}
}
