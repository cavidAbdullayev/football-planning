package org.example.footballplanning.bean.user.showReceivedRequests;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.example.footballplanning.bean.user.getUser.GetUserResponseBean;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class ShowReceivedRequestsResponseBean extends BaseResponseBean {
    GetUserResponseBean from;
    Integer playerCount;
    String message;
    String teamName;
    String timeStamp;
}
