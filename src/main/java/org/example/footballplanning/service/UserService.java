package org.example.footballplanning.service;

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

public interface UserService {
    ConfirmRegisterResponseBean confirmRegistration(String strToken);
    RegisterUserResponseBean register(RegisterUserRequestBean request);
    LoginUserWithPhoneNumberResponseBean loginWithPhoneNumber(LoginUserWithPhoneNumberRequestBean request);
    ChangePasswordResponseBean changePassword(ChangePasswordRequestBean request);
    ForgotPasswordResponseBean forgotPassword(ForgotPasswordRequestBean request);
    ConfirmForgotPasswordResponseBean confirmForgotPassword(String strToken, String newPassword);
    UpdateUserResponse update(UpdateUserRequest request);
}
