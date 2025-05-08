package org.example.footballplanning.controller;

import jakarta.validation.Valid;
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
import org.example.footballplanning.bean.user.payDebt.PayDebtRequestBean;
import org.example.footballplanning.bean.user.payDebt.PayDebtResponseBean;
import org.example.footballplanning.bean.user.update.UpdateUserRequest;
import org.example.footballplanning.bean.user.update.UpdateUserResponse;
import org.example.footballplanning.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/user")
public class UserController {
    UserService userService;

    @PutMapping("/change-password")
    public ResponseEntity<ChangePasswordResponseBean> changePassword(
            @RequestBody @Valid ChangePasswordRequestBean request) {
        return ResponseEntity.ok(userService.changePassword(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseBean> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequestBean request) {
        return ResponseEntity.ok(userService.forgotPassword(request));
    }

    @GetMapping("/confirm-forgot-password")
    public ResponseEntity<ConfirmForgotPasswordResponseBean> confirmForgotPassword(
            @RequestParam String strToken,
            @RequestParam String newPassword) {
        return ResponseEntity.ok(userService.confirmForgotPassword(strToken, newPassword));
    }

    @PutMapping("/update")
    public ResponseEntity<UpdateUserResponse> update(
            @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(request));
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<DeleteAccountResponseBean> deleteAccount(
            @RequestBody @Valid DeleteAccountRequestBean request) {
        return ResponseEntity.ok(userService.deleteAccount(request));
    }

    @GetMapping("/confirm-delete-account")
    public ResponseEntity<ConfirmDeleteAccountResponseBean> confirmDeleteAccount(
            @RequestParam String strToken) {
        return ResponseEntity.ok(userService.confirmDeleteAccount(strToken));
    }

    @PostMapping("/pay-debt")
    public ResponseEntity<PayDebtResponseBean> payDebt(
            @RequestBody PayDebtRequestBean request) {
        return ResponseEntity.ok(userService.payDebt(request));
    }
}
