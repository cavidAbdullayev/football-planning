package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.user.changePassword.ChangePasswordRequestBean;
import org.example.footballplanning.bean.user.changePassword.ChangePasswordResponseBean;
import org.example.footballplanning.bean.user.confirmDeleteAccount.ConfirmDeleteAccountResponseBean;
import org.example.footballplanning.bean.user.confirmForgotPassword.ConfirmForgotPasswordResponseBean;
import org.example.footballplanning.bean.user.deleteAccount.DeleteAccountRequestBean;
import org.example.footballplanning.bean.user.deleteAccount.DeleteAccountResponseBean;
import org.example.footballplanning.bean.user.forgotPassword.ForgotPasswordRequestBean;
import org.example.footballplanning.bean.user.forgotPassword.ForgotPasswordResponseBean;
import org.example.footballplanning.bean.user.loginWithPhoneNumber.LoginUserWithPhoneNumberRequestBean;
import org.example.footballplanning.bean.user.loginWithPhoneNumber.LoginUserWithPhoneNumberResponseBean;
import org.example.footballplanning.bean.user.payDebt.PayDebtRequestBean;
import org.example.footballplanning.bean.user.payDebt.PayDebtResponseBean;
import org.example.footballplanning.bean.user.update.UpdateUserRequest;
import org.example.footballplanning.bean.user.update.UpdateUserResponse;
import org.example.footballplanning.enums.PaymentMethodEnum;
import org.example.footballplanning.exception.customExceptions.*;
import org.example.footballplanning.helper.EmailServiceHelper;
import org.example.footballplanning.helper.TokenServiceHelper;
import org.example.footballplanning.helper.UserServiceHelper;
import org.example.footballplanning.model.child.*;
import org.example.footballplanning.repository.*;
import org.example.footballplanning.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.example.footballplanning.enums.TokenTypeEnum.*;
import static org.example.footballplanning.util.GeneralUtil.*;
import static org.example.footballplanning.util.GeneralUtil.containsNullOrEmptyValue;
import static org.example.footballplanning.staticData.GeneralStaticData.currentUserId;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    TokenServiceHelper tokenServiceHelper;
    UserRepository userRepository;
    TokenRepository tokenRepository;
    EmailServiceHelper emailServiceHelper;
    PaymentRepository paymentRepository;
    AnnouncementRepository announcementRepository;
    TeamRepository teamRepository;
    RequestRepository requestRepository;
    UserServiceHelper userServiceHelper;

    @Override
    public LoginUserWithPhoneNumberResponseBean loginWithPhoneNumber(@NotNull LoginUserWithPhoneNumberRequestBean request) {
        return null;
    }

    @Override
    @Transactional
    public ChangePasswordResponseBean changePassword(@NotNull ChangePasswordRequestBean request) {
        String newPassword = request.getNewPassword();
        String oldPassword = request.getOldPassword();
        String repeatNewPassword = request.getRepeatNewPassword();

        if (containsNullOrEmptyValue(newPassword, oldPassword, repeatNewPassword)) {
            throw new ValidationException("Invalid request body!");
        }
        if (!newPassword.equals(repeatNewPassword)) {
            throw new PasswordException("New password and its repeat doesn't match!");
        }


        UserEnt user = userServiceHelper.getUserById(currentUserId);

        if (!(oldPassword.equals(user.getPassword()))) {
            throw new PasswordException("Password is incorrect!");
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        return createResponse(new ChangePasswordResponseBean(), "Password updated successfully!");
    }

    @Override
    @Transactional
    public ForgotPasswordResponseBean forgotPassword(@NotNull ForgotPasswordRequestBean request) {
        validateFields(request);

        String email = request.getEmail();
        String newPassword = request.getNewPassword();
        String repeatPassword = request.getRepeatPassword();

        if (containsNullOrEmptyValue(email, newPassword, repeatPassword)) {
            throw new ValidationException("Invalid request body!");
        }

        if (!newPassword.equals(repeatPassword)) {
            throw new PasswordException("New password and its repeat doesn't match!");
        }

        UserEnt user = userRepository.findByEmailAndState(email, 1).orElseThrow(() ->
                new ObjectNotFoundException("User with the given email not found!"));

        Token token = user.getTokens().stream()
                .filter(t -> t.getUsedFor().equals(FORGOT_PASSWORD) && !t.isUsed())
                .findFirst()
                .orElseGet(() -> tokenServiceHelper.generateToken(user, FORGOT_PASSWORD));

        token.setExpireTime(LocalDateTime.now().plusMinutes(15));
        token.setStrToken(UUID.randomUUID().toString());

        tokenRepository.save(token);

        Map<String, Object> data = Map.of("newPassword", newPassword, "token", token.getStrToken());

        String message = emailServiceHelper.sendEmailWithToken(data, email);

        return createResponse(new ForgotPasswordResponseBean(), message);
    }

    @Override
    @Transactional
    public ConfirmForgotPasswordResponseBean confirmForgotPassword(@NotNull String strToken, String newPassword) {
        Token token = tokenServiceHelper.checkAndGetToken(strToken);

        if (token.isUsed() || token.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new SessionExpiredException("Token is expired or already used!");
        }

        UserEnt user = token.getUser();

        user.setPassword(newPassword);
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        return createResponse(new ConfirmForgotPasswordResponseBean(), "Password reset!");
    }

    @Override
    @Transactional
    public UpdateUserResponse update(@NotNull UpdateUserRequest request) {
        UpdateUserResponse response = new UpdateUserResponse();

        UserEnt user = userServiceHelper.getUserById(currentUserId);

        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(strToDate(request.getDateOfBirth()));
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumberAndState(request.getPhoneNumber(), 1)) {
                throw new ObjectAlreadyExistsException("Phone number already exists!");
            }
        }

        mapFields(user, request);
        mapFields(response, user);

        response.setDateOfBirth(dateToStr(strToDate(request.getDateOfBirth())));
        response.setMessage("User updated successfully!");

        return response;
    }

    @Override
    @Transactional
    public DeleteAccountResponseBean deleteAccount(DeleteAccountRequestBean request) {
        DeleteAccountResponseBean response = new DeleteAccountResponseBean();

        validateFields(request);

        UserEnt user = userServiceHelper.getUserById(currentUserId);

        if (user.getDebt() != null && user.getDebt() > 0) {
            throw new DebtException("You have debt! Firstly pay your debt before deleting your account!");
        }

        Token tokenEnt = user.getTokens().stream()
                .filter(t -> t.getUsedFor().equals(DELETE_ACCOUNT))
                .findFirst()
                .orElseGet(() -> tokenServiceHelper.generateToken(user, DELETE_ACCOUNT));

        tokenEnt.setExpireTime(LocalDateTime.now().plusMinutes(15));
        tokenEnt.setStrToken(UUID.randomUUID().toString());

        tokenRepository.save(tokenEnt);

        Map<String, Object> data = Map.of("token", tokenEnt.getStrToken(), "username", user.getUsername());

        String message = emailServiceHelper.sendEmailWithToken(data, user.getEmail());

        response.setMessage(message);
        return response;
    }

    @Override
    public ConfirmDeleteAccountResponseBean confirmDeleteAccount(String strToken) {
        ConfirmDeleteAccountResponseBean response = new ConfirmDeleteAccountResponseBean();

        Token token = tokenServiceHelper.checkAndGetToken(strToken);
        UserEnt user = token.getUser();

        tokenRepository.delete(token);

        user.setState(0);

        userRepository.updateUserState(user.getId(), 0);
        teamRepository.delete(user.getTeam());
        announcementRepository.updateAnnouncementsStateByUser(user.getId(), 0);
        requestRepository.updateRequestsStateBySenderOrReceiver(user.getId(), 0);

        user.setHasEverDeleted(true);
        userRepository.save(user);

        response.setMessage("Your account deleted successfully!");
        response.setUsername(user.getUsername());

        return response;
    }

    @Override
    @Transactional
    public PayDebtResponseBean payDebt(PayDebtRequestBean request) {
        validateFields(request);

        UserEnt user = userServiceHelper.getUserById(currentUserId);

        Double debt = user.getDebt();

        if (debt == null || debt == 0) {
            throw new DebtException("You have not any debt!");
        }

        Double amount = request.getAmount();

        if (amount <= 0) {
            throw new ValidationException("Invalid amount!");
        }

        double remainingDebt = Math.max(0, debt - amount);
        user.setDebt(remainingDebt);

        PaymentMethodEnum paymentMethodEnum = Arrays.stream(PaymentMethodEnum.values())
                .filter(method -> method.name().equalsIgnoreCase(request.getPaymentMethod()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Invalid payment method!"));

        PaymentEnt payment = mapFields(new PaymentEnt(), request);
        payment.setPaymentMethod(paymentMethodEnum);
        payment.setUser(user);
        paymentRepository.save(payment);

        userRepository.save(user);

        PayDebtResponseBean response = new PayDebtResponseBean();
        response.setMessage("Your debt has been successfully paid! Remaining debt: " + remainingDebt + "AZN");
        return response;
    }
}