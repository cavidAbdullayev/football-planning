package org.example.footballplanning.service;

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


public interface UserService {
    LoginUserWithPhoneNumberResponseBean loginWithPhoneNumber(LoginUserWithPhoneNumberRequestBean request);
    ChangePasswordResponseBean changePassword(ChangePasswordRequestBean request);
    ForgotPasswordResponseBean forgotPassword(ForgotPasswordRequestBean request);
    ConfirmForgotPasswordResponseBean confirmForgotPassword(String strToken, String newPassword);
    UpdateUserResponse update(UpdateUserRequest request);
    DeleteAccountResponseBean deleteAccount(DeleteAccountRequestBean request);
    ConfirmDeleteAccountResponseBean confirmDeleteAccount(String strToken);
    PayDebtResponseBean payDebt(PayDebtRequestBean request);
}
