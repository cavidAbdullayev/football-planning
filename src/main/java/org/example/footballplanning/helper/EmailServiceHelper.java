package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.enums.EmailSubjectEnum;
import org.example.footballplanning.enums.TokenTypeEnum;
import org.example.footballplanning.model.child.Token;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.net.URLEncoder.encode;
import static org.example.footballplanning.enums.TokenTypeEnum.*;

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
    static final String DELETE_ACCOUNT_URL = "/user/confirm-delete-account";
    static final String TOKEN_PARAM = "strToken=";
    static final String USERNAME_PARAM="username=";
    static final String PASSWORD_PARAM = "newPassword=";
    static final String FOLLOW_LINK_MESSAGE = "Please, follow the link below to complete your ";
    static final String CONFIRMATION_LINK_SENT = "Confirmation link sent to ";

    private final UserRepository userRepository;

    public String sendEmailWithToken(Map<String, Object> data, String email) {
        String response;
        Token token = (Token) data.get("token");
        TokenTypeEnum tokenType = token.getUsedFor();
        String serviceUrl = getServiceUrl(tokenType);

        if (serviceUrl == null) {
            return "Email has not been sent!";
        }
        String url = generateUrl(data);
        SimpleMailMessage message = new SimpleMailMessage();
        String text=FOLLOW_LINK_MESSAGE + tokenType.name().toLowerCase() + ": " + url;
        sendEmail(email,text,tokenType.name());
        response = CONFIRMATION_LINK_SENT + email + "\n" + FOLLOW_LINK_MESSAGE + tokenType.name().toLowerCase() + "!";
        return response;
    }

    public void sendEmailForDebt(String email, Double debt){
        UserEnt user=userRepository.findByEmailAndState(email,1).orElseThrow(()->new RuntimeException("User not found!"));

        String firstName=user.getFirstName();

        String text="Hi, "+firstName+"! You have total "+debt+"$ debt. Please, pay your debt as soon as. \nOtherwise, you cannot share any announcement!";

        sendEmail(email,text, EmailSubjectEnum.DEBT.name());
    }




    private String getServiceUrl(TokenTypeEnum tokenType) {
        if (tokenType == REGISTRATION) {
            return CONFIRM_REGISTER_URL;
        } else if (tokenType == FORGOT_PASSWORD) {
            return CONFIRM_FORGOT_PASSWORD_URL;
        }else if(tokenType==DELETE_ACCOUNT){
            return DELETE_ACCOUNT_URL;
        }
        return null;
    }
    private void sendEmail(String email,String text,String subject){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setText(text);
        message.setSubject(subject);
        message.setFrom(ownEmail);
        message.setTo(email);
        mailSender.send(message);
    }
    private String generateUrl(Map<String, Object> data) {
        Token token = (Token) data.get("token");
        TokenTypeEnum tokenType = token.getUsedFor();
        String strToken = token.getStrToken();
        String serviceUrl = getServiceUrl(tokenType);
        String url = http + host + ":" + port + serviceUrl;
        if (tokenType.name().equalsIgnoreCase("registration")||tokenType.name().equalsIgnoreCase("delete_account")) {
            url += "?" + TOKEN_PARAM + encode(strToken);
        } else if (tokenType.name().equalsIgnoreCase("forgot_password")) {
            String newPassword = (String) data.get("newPassword");
            url += "?" + TOKEN_PARAM + encode(strToken) + "&" + PASSWORD_PARAM + encode(newPassword);
    }
        return url;
    }
}