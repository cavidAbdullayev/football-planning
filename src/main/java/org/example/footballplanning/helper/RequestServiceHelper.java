package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.request.get.GetRequestResponseBean;
import org.example.footballplanning.model.child.RequestEnt;
import org.springframework.stereotype.Component;

import static org.example.footballplanning.helper.GeneralHelper.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RequestServiceHelper {
    UserServiceHelper userServiceHelper;

    public GetRequestResponseBean getRequestResponse(RequestEnt request) {
        return GetRequestResponseBean.builder()
                .to(userServiceHelper.getUserResponse(request.getTo()))
                .from(userServiceHelper.getUserResponse(request.getFrom()))
                .announcementId(request.getAnnouncement().getId())
                .timeStamp(dateTimeToStr(request.getCreatedDate()))
                .message(request.getMessage())
                .playerCount(request.getPlayerCount())
                .build();
    }
}