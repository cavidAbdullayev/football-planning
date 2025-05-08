package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.user.getUser.GetUserResponseBean;
import org.example.footballplanning.exception.customExceptions.ObjectNotFoundException;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.UserRepository;
import org.springframework.stereotype.Component;

import static org.example.footballplanning.util.GeneralUtil.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserServiceHelper {
    UserRepository userRepository;

    public GetUserResponseBean getUserResponse(UserEnt user) {
        return GetUserResponseBean.builder()
                .dateOfBirth(dateToStr(user.getDateOfBirth()))
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public UserEnt getUserById(String id) {
        return userRepository.findByIdAndState(id, 1)
                .orElseThrow(() -> new ObjectNotFoundException("User not found!"));
    }
}