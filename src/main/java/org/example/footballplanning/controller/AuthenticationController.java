//package org.example.footballplanning.controller;
//
//import jakarta.validation.Valid;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.example.footballplanning.bean.jwt.authentication.JwtAuthenticationResponse;
//import org.example.footballplanning.bean.jwt.signin.SigninRequest;
//import org.example.footballplanning.bean.jwt.signup.SignupRequest;
//import org.example.footballplanning.bean.user.confirmRegister.ConfirmRegisterResponseBean;
//import org.example.footballplanning.service.AuthenticationService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
//public class AuthenticationController {
//    AuthenticationService authenticationService;
//
//    @PostMapping("/signup")
//    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody @Valid SignupRequest request){
//        return ResponseEntity.ok(authenticationService.signup(request));
//    }
//    @PostMapping("/signin")
//    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody @Valid SigninRequest request){
//        return ResponseEntity.ok(authenticationService.signin(request));
//    }
//
//    @GetMapping("/confirm-registration")
//    public ResponseEntity<ConfirmRegisterResponseBean> confirmRegistration(@RequestParam("strToken") String strToken){
//        return ResponseEntity.ok(authenticationService.confirmRegistration(strToken));
//    }
//}
