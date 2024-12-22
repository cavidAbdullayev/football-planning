package org.example.footballplanning.bean.user.changePassword;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChangePasswordRequestBean {
    String username;
    String oldPassword;
    String newPassword;
    String repeatNewPassword;
}
