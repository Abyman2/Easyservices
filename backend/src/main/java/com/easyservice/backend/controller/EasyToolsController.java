package com.easyservice.backend.controller;

import com.easyservice.backend.service.EasyToolsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/tools")
public class EasyToolsController {

    private final EasyToolsService easyToolsService;

    public EasyToolsController(EasyToolsService easyToolsService) {
        this.easyToolsService = easyToolsService;
    }

    @GetMapping("/split-equal")
    public ResponseEntity<BigDecimal> splitEqual(@RequestParam BigDecimal totalAmount, @RequestParam int count) {
        BigDecimal perPerson = easyToolsService.calculateEqualSplit(totalAmount, count);
        return ResponseEntity.ok(perPerson);
    }

    @PostMapping("/spin-wheel")
    public ResponseEntity<String> spinWheel(@RequestBody List<String> participants) {
        String selected = easyToolsService.selectRandomPayer(participants);
        return ResponseEntity.ok(selected);
    }
}
