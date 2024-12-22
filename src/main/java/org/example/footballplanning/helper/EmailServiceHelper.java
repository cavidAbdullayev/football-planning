package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.enums.TokenType;
import org.example.footballplanning.model.child.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Map;

import static java.net.URLEncoder.encode;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailServiceHelper {
    final JavaMailSender mailSender;
    @Value("${app.server.host}")
    private String host;
    @Value("${app.server.http}")
    private String http;
    @Value("${app.email.sender}")
    private String ownEmail;
    @Value("${server.port}")
    private int port;
    static final String CONFIRM_REGISTER_URL = "/user/confirm-register";
    static final String CONFIRM_FORGOT_PASSWORD_URL = "/user/confirm-forgot-password";
    static final String TOKEN_PARAM = "strToken=";
    static final String PASSWORD_PARAM = "newPassword=";
    static final String FOLLOW_LINK_MESSAGE = "Please, follow the link below to complete your ";
    static final String CONFIRMATION_LINK_SENT = "Confirmation link sent to ";

    public String sendEmail(Map<String, Object> data, String email) {
        String response;
        Token token = (Token) data.get("token");
        TokenType tokenType = token.getUsedFor();
        String serviceUrl = getServiceUrl(tokenType);

        if (serviceUrl == null) {
            return "Email has not been sent!";
        }
        String url = generateUrl(data);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText(FOLLOW_LINK_MESSAGE + tokenType.name().toLowerCase() + ": " + url);
        message.setSubject(tokenType.name());
        message.setFrom(ownEmail);
        message.setTo(email);
        mailSender.send(message);
        response = CONFIRMATION_LINK_SENT + email + "\n" + FOLLOW_LINK_MESSAGE + tokenType.name().toLowerCase() + "!";
        return response;
    }

    private String getServiceUrl(TokenType tokenType) {
        if (tokenType == TokenType.REGISTRATION) {
            return CONFIRM_REGISTER_URL;
        } else if (tokenType == TokenType.FORGOT_PASSWORD) {
            return CONFIRM_FORGOT_PASSWORD_URL;
        }
        return null;
    }

    private String generateUrl(Map<String, Object> data) {
        Token token = (Token) data.get("token");
        TokenType tokenType = token.getUsedFor();
        String strToken = token.getStrToken();
        String serviceUrl = getServiceUrl(tokenType);
        String url = http + host + ":" + port + serviceUrl;
        if (tokenType.name().equalsIgnoreCase("registration")) {
            url += "?" + TOKEN_PARAM + encode(strToken);
        } else if (tokenType.name().equalsIgnoreCase("forgot_password")) {
            String newPassword = (String) data.get("newPassword");
            url += "?" + TOKEN_PARAM + encode(strToken) + "&" + PASSWORD_PARAM + encode(newPassword);
        }
        return url;
    }
}