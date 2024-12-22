package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.user.changePassword.ChangePasswordRequestBean;
import org.example.footballplanning.bean.user.changePassword.ChangePasswordResponseBean;
import org.example.footballplanning.bean.user.confirmForgotPassword.ConfirmForgotPasswordResponseBean;
import org.example.footballplanning.bean.user.confirmRegister.ConfirmRegisterResponseBean;
import org.example.footballplanning.bean.user.forgotPassword.ForgotPasswordRequestBean;
import org.example.footballplanning.bean.user.forgotPassword.ForgotPasswordResponseBean;
import org.example.footballplanning.bean.user.loginWithPhoneNumber.LoginUserWithPhoneNumberRequestBean;
import org.example.footballplanning.bean.user.loginWithPhoneNumber.LoginUserWithPhoneNumberResponseBean;
import org.example.footballplanning.bean.user.register.RegisterUserRequestBean;
import org.example.footballplanning.bean.user.register.RegisterUserResponseBean;
import org.example.footballplanning.bean.user.update.UpdateUserRequest;
import org.example.footballplanning.bean.user.update.UpdateUserResponse;
import org.example.footballplanning.helper.EmailServiceHelper;
import org.example.footballplanning.helper.TokenServiceHelper;
import org.example.footballplanning.model.child.Token;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.TokenRepository;
import org.example.footballplanning.repository.UserRepository;
import org.example.footballplanning.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Objects.*;
import static org.example.footballplanning.enums.TokenType.*;
import static org.example.footballplanning.helper.GeneralHelper.*;
import static org.example.footballplanning.helper.GeneralHelper.containsNullOrEmptyValue;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    TokenServiceHelper tokenServiceHelper;
    UserRepository userRepository;
    TokenRepository tokenRepository;
    EmailServiceHelper emailServiceHelper;

    @SneakyThrows
    @Override
    @Transactional
    public RegisterUserResponseBean register(@NotNull RegisterUserRequestBean request) {
        RegisterUserResponseBean response = new RegisterUserResponseBean();
        validateRequest(request);

        String username = request.getUsername();
        String email = request.getEmail();
        String phoneNumber = request.getPhoneNumber();
        Integer year = request.getYear();
        Integer month = request.getMonth();
        Integer day = request.getDay();
        LocalDate birthDate = LocalDate.of(year, month, day);

        if (userRepository.existsByEmailAndState(email, 1)) {
            throw new RuntimeException("Email already exists!");
        }
        if (userRepository.existsByUsernameAndState(username, 1)) {
            throw new RuntimeException("Username already exists!");
        }
        if (userRepository.existsByPhoneNumberAndState(phoneNumber, 1)) {
            throw new RuntimeException("Phone number already exists!");
        }
        validateRequest(request);
        Optional<UserEnt> userOpt = userRepository.findByUsernameOrEmailOrPhoneNumberAndState(username, email, phoneNumber, 0);
        UserEnt user = userOpt.orElseGet(() -> mapFields(new UserEnt(), request));


        if (userOpt.isPresent()) {
            updateDifferentFields(user, request);
        }
        user.setDateOfBirth(birthDate);

        List<Token> tokens = Optional.ofNullable(user.getTokens()).orElse(new ArrayList<>());

        Token token = tokens.stream()
                .filter(t -> t.getUsedFor().equals(REGISTRATION))
                .findFirst()
                .orElseGet(() -> tokenServiceHelper.generateToken(user, REGISTRATION));

        token.setStrToken(UUID.randomUUID().toString());
        token.setExpireTime(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);
        tokenRepository.save(token);
        Map<String, Object> data = Map.of("token", token);
        String message = emailServiceHelper.sendEmail(data, email);
        response.setMessage(message);
        return response;
    }

    @Override
    public LoginUserWithPhoneNumberResponseBean loginWithPhoneNumber(@NotNull LoginUserWithPhoneNumberRequestBean request) {
        return null;
    }


    @SneakyThrows
    @Override
    @Transactional
    public ConfirmRegisterResponseBean confirmRegistration(@NotNull String strToken) {
        Token token = tokenRepository.findByStrTokenAndState(strToken, 1).orElse(null);
        if (isNull(token)) {
            throw new RuntimeException("Token not found!");
        }
        String message = tokenServiceHelper.checkToken(token);

        if (!isNullOrEmpty(message)) {
            throw new RuntimeException("");
        }
        UserEnt user = token.getUser();
        user.setState(1);
        userRepository.save(user);
        return ConfirmRegisterResponseBean.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .message("User registered successfully!")
                .build();
    }

    @Override
    @Transactional
    public ChangePasswordResponseBean changePassword(@NotNull ChangePasswordRequestBean request) {
        String username = request.getUsername();
        String newPassword = request.getNewPassword();
        String oldPassword = request.getOldPassword();
        String repeatNewPassword = request.getRepeatNewPassword();

        if (containsNullOrEmptyValue(username, newPassword, oldPassword, repeatNewPassword)) {
            throw new RuntimeException("Invalid request body!");
        }
        if (!newPassword.equals(repeatNewPassword)) {
            throw new RuntimeException("New password and its repeat doesn't match!");
        }
        UserEnt user = userRepository.findByUsernameAndState(username, 1).orElse(null);
        if (isNull(user)) {
            throw new RuntimeException("User given by username not found!");
        }
        if (!oldPassword.equals(user.getPassword())) {
            throw new RuntimeException("Password is incorrect!");
        }
        user.setPassword(newPassword);
        userRepository.save(user);

        return createResponse(new ChangePasswordResponseBean(), "Password updated successfully!");
    }

    @Override
    @Transactional
    public ForgotPasswordResponseBean forgotPassword(@NotNull ForgotPasswordRequestBean request) {
        String newPassword = request.getNewPassword();
        String username = request.getUsername();
        String repeatPassword = request.getRepeatPassword();
        if (containsNullOrEmptyValue(newPassword, username, repeatPassword)) {
            throw new RuntimeException("Invalid request body!");
        }
        if (!repeatPassword.equals(newPassword)) {
            throw new RuntimeException("New password and its repeat doesn't match!");
        }
        UserEnt user = userRepository.findByUsernameAndState(username, 1).orElse(null);
        if (isNull(user)) {
            throw new RuntimeException("User given by username not found!");
        }

        String email = user.getEmail();

        Token token = user.getTokens()
                .stream()
                .filter(t -> t.getUsedFor().equals(FORGOT_PASSWORD))
                .findFirst()
                .orElseGet(() -> tokenServiceHelper.generateToken(user, FORGOT_PASSWORD));

        token.setExpireTime(LocalDateTime.now().plusMinutes(5));
        token.setStrToken(UUID.randomUUID().toString());
        tokenRepository.save(token);
        Map<String, Object> data = Map.of("newPassword", newPassword, "token", token);
        String message = emailServiceHelper.sendEmail(data, email);
        return createResponse(new ForgotPasswordResponseBean(), message);
    }

    @Override
    @Transactional
    public ConfirmForgotPasswordResponseBean confirmForgotPassword(@NotNull String strToken, String newPassword) {
        Token token = tokenRepository.findByStrTokenAndState(strToken, 1).orElse(null);
        String message = tokenServiceHelper.checkToken(token);

        if (isNullOrEmpty(message)) {
            throw new RuntimeException(message);
        }
        if (isNull(token)) {
            throw new RuntimeException("Token not found!");
        }

        UserEnt user = token.getUser();

        user.setPassword(newPassword);
        userRepository.save(user);

        return createResponse(new ConfirmForgotPasswordResponseBean(), "Password reset!");
    }

    @Override
    @Transactional
    public UpdateUserResponse update(@NotNull UpdateUserRequest request) {
        UpdateUserResponse response = new UpdateUserResponse();
        String username = request.getUsername();
        String dateOfBirth = request.getDateOfBirth();
        if (isNullOrEmpty(username)) {
            throw new RuntimeException("Invalid request body!");
        }

        Optional<UserEnt> userOpt = userRepository.findByUsernameAndState(username, 1);
        UserEnt user = userOpt.orElseThrow(() -> new RuntimeException("User not found!"));
        if (dateOfBirth != null) {
            user.setDateOfBirth(strToDate(dateOfBirth));
        }
        if (userRepository.existsByPhoneNumberAndState(request.getPhoneNumber(), 1) && !user.getPhoneNumber().equals(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists!");
        }
        mapFields(user, request);
        mapFields(response, user);
        response.setMessage("User updated successfully!");
        return response;
    }
}