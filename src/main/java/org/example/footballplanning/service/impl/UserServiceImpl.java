package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.user.changePassword.ChangePasswordRequestBean;
import org.example.footballplanning.bean.user.changePassword.ChangePasswordResponseBean;
import org.example.footballplanning.bean.user.confirmDeleteAccount.ConfirmDeleteAccountResponseBean;
import org.example.footballplanning.bean.user.confirmForgotPassword.ConfirmForgotPasswordResponseBean;
import org.example.footballplanning.bean.user.confirmRegister.ConfirmRegisterResponseBean;
import org.example.footballplanning.bean.user.deleteAccount.DeleteAccountRequestBean;
import org.example.footballplanning.bean.user.deleteAccount.DeleteAccountResponseBean;
import org.example.footballplanning.bean.user.forgotPassword.ForgotPasswordRequestBean;
import org.example.footballplanning.bean.user.forgotPassword.ForgotPasswordResponseBean;
import org.example.footballplanning.bean.user.getUser.GetUserResponseBean;
import org.example.footballplanning.bean.user.loginWithPhoneNumber.LoginUserWithPhoneNumberRequestBean;
import org.example.footballplanning.bean.user.loginWithPhoneNumber.LoginUserWithPhoneNumberResponseBean;
import org.example.footballplanning.bean.user.payDebt.PayDebtRequestBean;
import org.example.footballplanning.bean.user.payDebt.PayDebtResponseBean;
import org.example.footballplanning.bean.user.register.RegisterUserRequestBean;
import org.example.footballplanning.bean.user.register.RegisterUserResponseBean;
import org.example.footballplanning.bean.user.showReceivedRequests.ShowReceivedRequestsResponseBean;
import org.example.footballplanning.bean.user.showSentRequests.ShowSentRequestsResponseBean;
import org.example.footballplanning.bean.user.update.UpdateUserRequest;
import org.example.footballplanning.bean.user.update.UpdateUserResponse;
import org.example.footballplanning.enums.PaymentMethodEnum;
import org.example.footballplanning.helper.EmailServiceHelper;
import org.example.footballplanning.helper.TokenServiceHelper;
import org.example.footballplanning.model.child.*;
import org.example.footballplanning.repository.*;
import org.example.footballplanning.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Objects.*;
import static org.example.footballplanning.enums.TokenTypeEnum.*;
import static org.example.footballplanning.helper.GeneralHelper.*;
import static org.example.footballplanning.helper.GeneralHelper.containsNullOrEmptyValue;
import static org.example.footballplanning.staticData.GeneralStaticData.*;


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
    MatchRepository matchRepository;
    TeamRepository teamRepository;
    RequestRepository requestRepository;

    @SneakyThrows
    @Override
    @Transactional
    public RegisterUserResponseBean register(@NotNull RegisterUserRequestBean request) {
        RegisterUserResponseBean response = new RegisterUserResponseBean();
        validateFields(request);

        String username = request.getUsername();
        String email = request.getEmail();
        String phoneNumber = request.getPhoneNumber();
        Integer year = request.getYear();
        Integer month = request.getMonth();
        Integer day = request.getDay();
        LocalDate birthDate = LocalDate.of(year, month, day);

        validateFields(request);
        if (userRepository.existsByEmailAndState(email, 1)) {
            throw new RuntimeException("Email already exists!");
        }
        if (userRepository.existsByUsernameAndState(username, 1)) {
            throw new RuntimeException("Username already exists!");
        }
        if (userRepository.existsByPhoneNumberAndState(phoneNumber, 1)) {
            throw new RuntimeException("Phone number already exists!");
        }

        Optional<UserEnt> userOpt = userRepository.findByEmailAndPhoneNumberAndUsernameAndState(username, email, phoneNumber, 0);
        UserEnt user = userOpt.orElseGet(() -> mapFields(new UserEnt(), request));


        if (userOpt.isPresent()) {
            updateDifferentFields(user, request);
        }

        user.setDateOfBirth(birthDate);
        user.setState(0);

        Token token = tokenServiceHelper.getOrGenerateToken(user,REGISTRATION);

        userRepository.save(user);
        tokenRepository.save(token);
        Map<String, Object> data = Map.of("token", token);
        String message = emailServiceHelper.sendEmailWithToken(data, email);
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
        ConfirmRegisterResponseBean response=new ConfirmRegisterResponseBean();

        Token token =tokenServiceHelper.checkToken(strToken);
        UserEnt user = token.getUser();
        List<AnnouncementEnt>announcements=user.getSharedAnnouncements();

        //return last announcements if don't expire
        announcements.stream().filter(announcement->
                LocalDateTime.now().plusHours(6).isBefore(announcement.getStartDate())).forEach(announcement-> announcement.setState(1));

        user.setState(1);
        userRepository.save(user);

        mapFields(response,user);
        response.setDateOfBirth(user.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        return createResponse(response, "User registered successfully!");
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
        String message = emailServiceHelper.sendEmailWithToken(data, email);
        return createResponse(new ForgotPasswordResponseBean(), message);
    }

    @Override
    @Transactional
    public ConfirmForgotPasswordResponseBean confirmForgotPassword(@NotNull String strToken, String newPassword) {
        Token token = tokenServiceHelper.checkToken(strToken);

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

        response.setDateOfBirth(dateToStr(strToDate(dateOfBirth)));
        response.setMessage("User updated successfully!");

        return response;
    }

    @Override
    @Transactional
    public DeleteAccountResponseBean deleteAccount(DeleteAccountRequestBean request) {
        DeleteAccountResponseBean response=new DeleteAccountResponseBean();

        validateFields(request);

        String username=request.getUsername();

        UserEnt user=userRepository.findByUsernameAndState(username,1).orElseThrow(()->new RuntimeException("User not exists by username!"));
        Double debt=user.getDebt();

        if(debt!=null&&debt>0){
            throw new RuntimeException("You have debt! Firstly pay your debt before deleting your account!");
        }

        Token token=user.getTokens().stream().filter(t->t.getUsedFor().equals(DELETE_ACCOUNT)).findFirst()
                .orElseGet(()->tokenServiceHelper.generateToken(user,DELETE_ACCOUNT));

        token.setExpireTime(LocalDateTime.now().plusMinutes(5));
        token.setStrToken(UUID.randomUUID().toString());

        Map<String, Object>data=Map.of("token",token,"username",username);

        String message=emailServiceHelper.sendEmailWithToken(data,user.getEmail());

        tokenRepository.save(token);

        response.setMessage(message);
        return response;
    }

    @Override
    public ConfirmDeleteAccountResponseBean confirmDeleteAccount(String strToken) {
        ConfirmDeleteAccountResponseBean response=new ConfirmDeleteAccountResponseBean();

        Token token=tokenServiceHelper.checkToken(strToken);

        UserEnt user=token.getUser();
        user.setState(0);

        TeamEnt team=user.getTeam();
        team.setState(0);

        List<AnnouncementEnt>announcements=user.getSharedAnnouncements();
        announcements.forEach(announcement -> announcement.setState(0));

        List<RequestEnt>sentRequests=user.getSentRequests();
        sentRequests.stream().filter(sentRequest->sentRequest.getState().equals(1))
                .forEach(sentRequest->sentRequest.setState(0));

        List<RequestEnt>receivedRequests=user.getReceivedRequests();
        receivedRequests.stream().filter(receivedRequest->receivedRequest.getState()==1)
                .forEach(receivedRequest->receivedRequest.setState(0));

        user.setHasEverDeleted(true);

        teamRepository.save(team);
        announcementRepository.saveAll(announcements);
        requestRepository.saveAll(sentRequests);
        requestRepository.saveAll(receivedRequests);
        userRepository.save(user);

        response.setMessage("Your account deleted successfully!");
        response.setUsername(user.getUsername());

        return response;
    }

    @Override
    @Transactional
    public PayDebtResponseBean payDebt(PayDebtRequestBean request) {
        PayDebtResponseBean response=new PayDebtResponseBean();
        validateFields(request);

        UserEnt user=userRepository.findByIdAndState(currentUserId,1).orElseThrow(()->new RuntimeException("User not found!"));

        Double debt=user.getDebt();

        if(debt==null||debt==0){
            throw new RuntimeException("You have not any debt!");
        }

        Double amount=request.getAmount();
        String paymentMethod=request.getPaymentMethod();

        if(amount<=0){
            throw new RuntimeException("Invalid amount!");
        }
        else {
            debt=debt<=amount?0:debt-amount;
            response.setMessage("Your debt paid successfully! Remaining debt: "+debt+"$");
        }

        PaymentMethodEnum paymentMethodEnum= Arrays.stream(PaymentMethodEnum.values()).filter(method->method.name().equalsIgnoreCase(paymentMethod)).findFirst()
                .orElseThrow(()->new RuntimeException("Invalid payment method!"));
        PaymentEnt payment=mapFields(new PaymentEnt(),request);
        payment.setPaymentMethod(paymentMethodEnum);
        payment.setUser(user);
        paymentRepository.save(payment);

        user.setDebt(debt);
        userRepository.save(user);

        return response;
    }

    @Override
    public List<ShowReceivedRequestsResponseBean> showReceivedRequests() {
        UserEnt user=userRepository.findByIdAndState(currentUserId,1).orElseThrow(()->new RuntimeException("User not found!"));
        List<RequestEnt>requests=user.getReceivedRequests();
        List<ShowReceivedRequestsResponseBean>response=new ArrayList<>();

        requests.stream().filter(request->request.getState()==1).forEach(request->{
            GetUserResponseBean userResponse=new GetUserResponseBean();
            ShowReceivedRequestsResponseBean requestResponse=new ShowReceivedRequestsResponseBean();
            UserEnt from=request.getFrom();
            mapFields(userResponse,from);
            userResponse.setDateOfBirth(dateToStr(from.getDateOfBirth()));
            requestResponse.setFrom(userResponse);
            requestResponse.setTeamName(from.getTeam().getTeamName());
            requestResponse.setMessage(request.getMessage());
            requestResponse.setPlayerCount(request.getPlayerCount());
            requestResponse.setTimeStamp(dateTimeToStr(request.getCreatedDate()));
            response.add(requestResponse);
        });

        return response;
    }

    @Override
    public List<ShowSentRequestsResponseBean> showSentRequests() {
        UserEnt user=userRepository.findByIdAndState(currentUserId,1).orElseThrow(()->new RuntimeException("User not found!"));
        List<RequestEnt>requests=user.getSentRequests();
        List<ShowSentRequestsResponseBean>response=new ArrayList<>();

        requests.stream().filter(request->request.getState()==1).forEach(request-> {
            GetUserResponseBean userResponse=new GetUserResponseBean();
            ShowSentRequestsResponseBean requestResponse=new ShowSentRequestsResponseBean();
            UserEnt to=request.getTo();
            mapFields(userResponse,to);
            userResponse.setDateOfBirth(dateToStr(to.getDateOfBirth()));
            requestResponse.setTo(userResponse);
            requestResponse.setTeamName(to.getTeam().getTeamName());
            requestResponse.setMessage(request.getMessage());
            requestResponse.setPlayerCount(request.getPlayerCount());
            requestResponse.setTimeStamp(dateTimeToStr(request.getCreatedDate()));
            response.add(requestResponse);
        });

        return response;
    }
}