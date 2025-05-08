package org.example.footballplanning.bean.request.get;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.example.footballplanning.bean.user.getUser.GetUserResponseBean;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
public class GetRequestResponseBean extends BaseResponseBean implements Serializable {
    String announcementId;
    GetUserResponseBean to;
    Integer playerCount;
    String message;
    String timeStamp;
    GetUserResponseBean from;
}