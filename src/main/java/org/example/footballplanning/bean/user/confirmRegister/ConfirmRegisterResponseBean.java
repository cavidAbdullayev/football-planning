package org.example.footballplanning.bean.user.confirmRegister;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.footballplanning.bean.base.BaseResponseBean;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmRegisterResponseBean extends BaseResponseBean {
    String firstName;
    String lastName;
    String username;
    String phoneNumber;
    String dateOfBirth;
}
