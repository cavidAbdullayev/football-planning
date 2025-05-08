package org.example.footballplanning.helper;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.enums.EmailSubjectEnum;
import org.example.footballplanning.enums.TokenTypeEnum;
import org.example.footballplanning.exception.customExceptions.ObjectNotFoundException;
import org.example.footballplanning.model.child.MatchEnt;
import org.example.footballplanning.model.child.Token;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.example.footballplanning.enums.EmailSubjectEnum.MATCH_POSTER;
import static org.example.footballplanning.enums.TokenTypeEnum.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailServiceHelper {
    final JavaMailSender mailSender;
    final MatchServiceHelper matchServiceHelper;
    @Value("${app.server.host}")
    private String host;
    @Value("${app.server.http}")
    private String http;
    @Value("${app.email.sender}")
    private String ownEmail;
    @Value("${server.port}")
    private int port;
    static final String CONFIRM_REGISTER_URL = "/auth/confirm-registration";
    static final String CONFIRM_FORGOT_PASSWORD_URL = "/user/confirm-forgot-password";
    static final String DELETE_ACCOUNT_URL = "/user/confirm-delete-account";
    static final String TOKEN_PARAM = "strToken";
    static final String PASSWORD_PARAM = "newPassword=";
    static final String FOLLOW_LINK_MESSAGE = "Please, follow the link below to complete your ";
    static final String CONFIRMATION_LINK_SENT = "Confirmation link sent to ";

    private final UserRepository userRepository;

    public String sendEmailWithToken(Map<String, Object> data, String email) {
        Token token = (Token) data.get("token");
        TokenTypeEnum tokenType = token.getUsedFor();
        String serviceUrl = getServiceUrl(tokenType);

        if (serviceUrl == null) {
            return "Email has not been sent!";
        }

        String url = generateUrl(data);
        String text = FOLLOW_LINK_MESSAGE + tokenType.name().toLowerCase() + ": " + url;
        sendEmail(email, text, tokenType.name());

        return CONFIRMATION_LINK_SENT + email + "\n" + FOLLOW_LINK_MESSAGE + tokenType.name().toLowerCase() + "!";
    }

    public void sendEmailForDebt(String email, Double debt) {
        UserEnt user = userRepository.findByEmailAndState(email, 1).orElseThrow(() -> new ObjectNotFoundException("User not found!"));

        String firstName = user.getFirstName();

        String text = "Hi, " + firstName + "! You have total " + debt + "$ debt. Please, pay your debt as soon as. \nOtherwise, you cannot share any announcement!";

        sendEmail(email, text, EmailSubjectEnum.DEBT.name());
    }


    private String getServiceUrl(TokenTypeEnum tokenType) {
        return switch (tokenType) {
            case REGISTRATION -> CONFIRM_REGISTER_URL;
            case FORGOT_PASSWORD -> CONFIRM_FORGOT_PASSWORD_URL;
            case DELETE_ACCOUNT -> DELETE_ACCOUNT_URL;
            default -> null;
        };
    }

    private void sendEmail(String email, String text, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText(text);
        message.setSubject(subject);
        message.setFrom(ownEmail);
        message.setTo(email);
        mailSender.send(message);
    }

    private String generateUrl(Map<String, Object> data) {
        Token token = (Token) data.get("token");
        TokenTypeEnum tokenTypeEnum = token.getUsedFor();
        String strToken = token.getStrToken();
        String serviceUrl = getServiceUrl(tokenTypeEnum);

        StringBuilder url = new StringBuilder(http)
                .append(host)
                .append(":")
                .append(port)
                .append(serviceUrl)
                .append("?")
                .append(TOKEN_PARAM)
                .append("=")
                .append(URLEncoder.encode(strToken, StandardCharsets.UTF_8));

        if (tokenTypeEnum == FORGOT_PASSWORD) {
            String newPassword = (String) data.get("newPassword");
            url.append("&")
                    .append(PASSWORD_PARAM)
                    .append("=")
                    .append(URLEncoder.encode(newPassword, StandardCharsets.UTF_8));
        }
        return url.toString();
    }
@SneakyThrows
    public void sendMatchPoster(MatchEnt match){
        byte[] pdfBytes=matchServiceHelper.generateHtmlPoster(match);

        String teamAEmail=match.getTeamA().getManagerUser().getEmail();
        String teamBEmail=match.getTeamB().getManagerUser().getEmail();

        MimeMessage message=mailSender.createMimeMessage();

        MimeMessageHelper helper=new MimeMessageHelper(message,true);
        helper.setTo(new String[]{teamAEmail,teamBEmail});
        helper.setSubject(MATCH_POSTER.toString());
        helper.setText("Hello! Please find attached your match poster. Good luck!",false);
        helper.addAttachment("MatchPoster.pdf",new ByteArrayDataSource(pdfBytes,"application/pdf"));

        mailSender.send(message);
    }
}