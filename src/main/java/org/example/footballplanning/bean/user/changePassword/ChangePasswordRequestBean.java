package org.example.footballplanning.bean.user.changePassword;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChangePasswordRequestBean {
    String oldPassword;
    String newPassword;
    String repeatNewPassword;
}
