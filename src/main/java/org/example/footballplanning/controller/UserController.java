package org.example.footballplanning.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.user.changePassword.ChangePasswordRequestBean;
import org.example.footballplanning.bean.user.changePassword.ChangePasswordResponseBean;
import org.example.footballplanning.bean.user.confirmForgotPassword.ConfirmForgotPasswordResponseBean;
import org.example.footballplanning.bean.user.confirmRegister.ConfirmRegisterResponseBean;
import org.example.footballplanning.bean.user.forgotPassword.ForgotPasswordRequestBean;
import org.example.footballplanning.bean.user.forgotPassword.ForgotPasswordResponseBean;
import org.example.footballplanning.bean.user.register.RegisterUserRequestBean;
import org.example.footballplanning.bean.user.register.RegisterUserResponseBean;
import org.example.footballplanning.bean.user.update.UpdateUserRequest;
import org.example.footballplanning.bean.user.update.UpdateUserResponse;
import org.example.footballplanning.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/user")
public class UserController {
    UserService userService;
    @PostMapping("/register")
    public RegisterUserResponseBean registration(@RequestBody @Valid RegisterUserRequestBean request){
        return userService.register(request);
    }
    @GetMapping("/confirm-register")
    public ConfirmRegisterResponseBean confirmRegister(@RequestParam String strToken){
        return userService.confirmRegistration(strToken);
    }
    @PutMapping("/change-password")
    public ChangePasswordResponseBean changePassword(@RequestBody @Valid ChangePasswordRequestBean request){
        return userService.changePassword(request);
    }
    @PostMapping("/forgot-password")
    public ForgotPasswordResponseBean forgotPassword(@RequestBody @Valid ForgotPasswordRequestBean request){
        return userService.forgotPassword(request);
    }
    @GetMapping("/confirm-forgot-password")
    public ConfirmForgotPasswordResponseBean confirmForgotPassword(@RequestParam(name = "strToken") String strToken,
                                                                   @RequestParam(name = "newPassword") String newPassword){
        return userService.confirmForgotPassword(strToken,newPassword);
    }
    @PutMapping("/update")
    public UpdateUserResponse update(@RequestBody @Valid UpdateUserRequest request){
        return userService.update(request);
    }
}
