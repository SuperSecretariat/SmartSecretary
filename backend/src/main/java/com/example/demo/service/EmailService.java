package com.example.demo.service;

import com.example.demo.exceptions.EmailSendingException;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {
    private final String sendGridApiKey;

    @Autowired
    public EmailService(@Value("${sendgrid.api.key}") String apiKey){
        this.sendGridApiKey = apiKey;
    }

    public void sendEmail(String toEmailString, String subject, String contextText) throws EmailSendingException{
        Email fromEmail = new Email("smartsecretarynoreply@gmail.com");
        Email toEmail = new Email(toEmailString);

        Content content = new Content("text/plain", contextText);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        SendGrid sendGrid = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 400) {
                throw new EmailSendingException("Failed to send email: " + response.getStatusCode() + " - " + response.getBody());
            }

        } catch (IOException ex) {
            throw new EmailSendingException("IOException occurred while sending email");
        }
    }
}
