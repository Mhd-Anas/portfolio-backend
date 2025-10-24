package com.portfolio.backend.controller;

import com.portfolio.backend.model.ContactMessage;
import com.portfolio.backend.service.GoogleSheetsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/contact")
public class ContactMessageController {

    private final GoogleSheetsService sheetsService;
    private static final Logger logger = LoggerFactory.getLogger(ContactMessageController.class);

    public ContactMessageController(GoogleSheetsService sheetsService) {
        this.sheetsService = sheetsService;
    }

    @PostMapping
    public String sendMessage(@RequestBody ContactMessage message) {
        try {
            logger.info("📨 Received contact from: {} ({})", message.getName(), message.getEmail());
            
            // Save to Google Sheets
            sheetsService.saveContactToSheet(
                message.getName(),
                message.getEmail(), 
                message.getSubject(),
                message.getMessage()
            );
            
            logger.info("✅ Contact saved successfully to Google Sheets");
            return "✅ Message sent successfully!";
            
        } catch (Exception e) {
            logger.error("💥 Error: " + e.getMessage());
            return "❌ Error: " + e.getMessage();
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public String health() {
        return "✅ Backend is healthy!";
    }
    
    // Test endpoint
    @GetMapping("/test")
    public String test() {
        return "✅ Contact API is working!";
    }
}