package org.example.footballplanning.bean.user.update;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
    @NotNull(message="Username cannot be null!")
    String username;
    String firstName;
    String lastName;
    String phoneNumber;
    String dateOfBirth;
}
