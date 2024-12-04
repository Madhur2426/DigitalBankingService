package com.hcltech.digitalbankingservice.controller;

import com.hcltech.digitalbankingservice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email/notify")
public class EmailNotifyController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/notify")
    public String sendTransactionNotification(
            @RequestParam String emailId,
            @RequestParam Double amount,
            @RequestParam String type,
            @RequestParam String account
    ) {
        emailService.sendTransactionEmail(emailId, type, amount,account);
        System.out.println("Email sent successfully to " + emailId);
        return "Email sent successfully to " + emailId;
    }
}