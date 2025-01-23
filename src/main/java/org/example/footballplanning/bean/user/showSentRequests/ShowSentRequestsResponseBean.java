package org.example.footballplanning.bean.user.showSentRequests;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.example.footballplanning.bean.user.getUser.GetUserResponseBean;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class ShowSentRequestsResponseBean extends BaseResponseBean {
    GetUserResponseBean to;
    Integer playerCount;
    String message;
    String teamName;
    String timeStamp;
}
