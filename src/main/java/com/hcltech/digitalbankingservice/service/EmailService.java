package com.hcltech.digitalbankingservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private SesClient sesClient;


    public void sendTransactionEmail(String toEmail, String type, Double amount, String maskedAccount) {
        String txnDate = LocalDateTime.now().toString();
        String subject = "Transaction Alert";
        String body = "Dear Customer,\n\n" +
                "Rs." + amount + " has been " + type + " from account **" + maskedAccount +
                " on " + txnDate + ". Your transaction reference number is " + "426637943386" + ".\n\n" +
                "If you did not authorize this transaction, please report it immediately by calling 18002586161 or SMS BLOCK UPI to 7308080808.\n\n" +
                "Warm Regards,\n" +
                "Digital Banking Service";

        Destination destination = Destination.builder()
                .toAddresses(toEmail)
                .build();

        Message message = Message.builder()
                .subject(Content.builder().data(subject).build())
                .body(Body.builder().text(Content.builder().data(body).build()).build())
                .build();

        SendEmailRequest request = SendEmailRequest.builder()
                .source("digitalbankingapplication@gmail.com")
                .destination(destination)
                .message(message)
                .build();

        sesClient.sendEmail(request);
    }
}
