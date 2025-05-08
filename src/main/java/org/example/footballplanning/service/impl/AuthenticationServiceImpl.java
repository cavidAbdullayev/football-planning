//package org.example.footballplanning.service.impl;
//
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.example.footballplanning.bean.jwt.authentication.JwtAuthenticationResponse;
//import org.example.footballplanning.bean.jwt.signin.SigninRequest;
//import org.example.footballplanning.bean.jwt.signup.SignupRequest;
//import org.example.footballplanning.bean.user.confirmRegister.ConfirmRegisterResponseBean;
//import org.example.footballplanning.enums.TokenTypeEnum;
//import org.example.footballplanning.exception.customExceptions.ObjectAlreadyExistsException;
//import org.example.footballplanning.exception.customExceptions.SessionExpiredException;
//import org.example.footballplanning.helper.EmailServiceHelper;
//import org.example.footballplanning.helper.TokenServiceHelper;
//import org.example.footballplanning.model.child.Token;
//import org.example.footballplanning.model.child.UserEnt;
//import org.example.footballplanning.repository.AnnouncementRepository;
//import org.example.footballplanning.repository.TokenRepository;
//import org.example.footballplanning.repository.UserRepository;
//import org.example.footballplanning.service.AuthenticationService;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.example.footballplanning.enums.RoleType.USER;
//import static org.example.footballplanning.util.GeneralUtil.*;
//
//@Service
//@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
//@RequiredArgsConstructor
//@Slf4j
//public class AuthenticationServiceImpl implements AuthenticationService {
//    UserRepository userRepository;
//    TokenServiceHelper tokenServiceHelper;
//    AnnouncementRepository announcementRepository;
//    EmailServiceHelper emailServiceHelper;
//    TokenRepository tokenRepository;
//
//    //    @Override
//    public JwtAuthenticationResponse signin(SigninRequest request) {
////        UserEnt user = userRepository.findByUsernameAndState(request.getUsername(), 1)
////                .orElseThrow(() -> new RuntimeException("Active user not found with username: " + request.getUsername()));
////
////        try {
////            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
////        } catch (BadCredentialsException e) {
////            log.warn("Failed login attempt for username: {}", request.getUsername());
////            throw new BadCredentialsException("Invalid username or password!");
////        }
////
////        String jwt = jwtServiceImpl.generateToken(user);
////
////        return JwtAuthenticationResponse.builder()
////                .message("Your token:")
////                .token(jwt)
////                .build();
////
//        return null;
//    }
//
//    @Override
//    public JwtAuthenticationResponse signup(SignupRequest request) {
//        validateFields(request);
//
//        String username = request.getUsername();
//        String email = request.getEmail();
//        String phoneNumber = request.getPhoneNumber();
//        LocalDate birthDate = LocalDate.of(request.getYear(), request.getMonth(), request.getDay());
//
//        checkUserIfExists(email, username, phoneNumber);
//
//        Optional<UserEnt> userOpt = userRepository.findByEmailAndPhoneNumberAndUsernameAndState(username, email, phoneNumber, 0);
//        UserEnt user = userOpt.orElseGet(() -> mapFields(new UserEnt(), request));
//
//
//        if (userOpt.isPresent()) {
//            updateDifferentFields(user, request);
//        }
//
//        user.setDateOfBirth(birthDate);
//        user.setState(0);
//        user.setPassword(request.getPassword());
//
//        userRepository.save(user);
//
//        Token token = tokenServiceHelper.getOrGenerateToken(user, TokenTypeEnum.REGISTRATION);
//        emailServiceHelper.sendEmailWithToken(Map.of("token", token), email);
//
//        tokenRepository.save(token);
//
//        return JwtAuthenticationResponse.builder()
//                .token(token.getStrToken())
//                .message("Your token:")
//                .build();
//    }
//
//    @SneakyThrows
//    @Override
//    @Transactional
//    public ConfirmRegisterResponseBean confirmRegistration(@NotNull String strToken) {
//        ConfirmRegisterResponseBean response = new ConfirmRegisterResponseBean();
//
//        Token token = tokenServiceHelper.checkAndGetToken(strToken);
//        UserEnt user = token.getUser();
//
//        if (token.getExpireTime().isBefore(LocalDateTime.now())) {
//            throw new SessionExpiredException("The token has expired.");
//        }
//
//        user.setState(1);
//        user.setActive(true);
//
//        Set<String> roles = new HashSet<>();
//        roles.add(USER.name());
//        user.setRoles(roles);
//
//        userRepository.save(user);
//
//        announcementRepository.updateAnnouncementsStateByUser(user.getId(), 1);
//
//        mapFields(response, user);
//        response.setDateOfBirth(user.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
//
//        return createResponse(response, "User registered successfully!");
//    }
//
//    private void checkUserIfExists(String email, String username, String phoneNumber) {
//        if (userRepository.existsByEmailAndState(email, 1)) {
//            throw new ObjectAlreadyExistsException("Email already exists!");
//        }
//        if (userRepository.existsByUsernameAndState(username, 1)) {
//            throw new ObjectAlreadyExistsException("Username already exists!");
//        }
//        if (userRepository.existsByPhoneNumberAndState(phoneNumber, 1)) {
//            throw new ObjectAlreadyExistsException("Phone number already exists!");
//        }
//    }
//}