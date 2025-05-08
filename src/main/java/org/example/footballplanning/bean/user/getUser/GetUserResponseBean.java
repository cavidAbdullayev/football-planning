package org.example.footballplanning.bean.user.getUser;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.footballplanning.bean.base.BaseResponseBean;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class GetUserResponseBean extends BaseResponseBean implements Serializable {
    String firstName;
    String lastName;
    String username;
    String phoneNumber;
    String dateOfBirth;
}
