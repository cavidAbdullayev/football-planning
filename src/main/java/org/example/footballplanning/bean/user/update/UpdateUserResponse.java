package org.example.footballplanning.bean.user.update;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserResponse extends BaseResponseBean {
    String firstName;
    String lastName;
    String phoneNumber;
    String dateOfBirth;
}
