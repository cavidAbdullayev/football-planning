package org.example.footballplanning.bean.user.loginWithPhoneNumber;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginUserWithPhoneNumberRequestBean {
    String phoneNumber;
    String password;
}
