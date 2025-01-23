package org.example.footballplanning.bean.user.getUser;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class GetUserResponseBean extends BaseResponseBean {
    String firstName;
    String lastName;
    String username;
    String password;
    String email;
    String phoneNumber;
    String dateOfBirth;
}
