package org.example.footballplanning.bean.user.register;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterUserRequestBean {
    String firstName;
    String lastName;
    String username;
    String password;
    String email;
    String phoneNumber;
    Integer year;
    Integer month;
    Integer day;
}
