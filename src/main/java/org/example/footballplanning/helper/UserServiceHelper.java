package org.example.footballplanning.helper;

import org.example.footballplanning.bean.user.getUser.GetUserResponseBean;
import org.example.footballplanning.model.child.MatchEnt;
import org.example.footballplanning.model.child.UserEnt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static org.example.footballplanning.helper.GeneralHelper.*;

@Component
public class UserServiceHelper {
    public GetUserResponseBean getUserResponse(UserEnt user) {
        return GetUserResponseBean.builder()
                .dateOfBirth(dateToStr(user.getDateOfBirth()))
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public List<MatchEnt> getFutureMatches(UserEnt user) {
        return Stream.concat(user.getTeam().getHomeMatch().stream(), user.getTeam().getAwayMatch().stream()).toList();
    }
}