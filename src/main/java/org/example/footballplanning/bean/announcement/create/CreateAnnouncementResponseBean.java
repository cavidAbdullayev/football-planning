package org.example.footballplanning.bean.announcement.create;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.example.footballplanning.bean.stadium.get.GetStadiumResponseBean;
import org.example.footballplanning.bean.user.getUser.GetUserResponseBean;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class CreateAnnouncementResponseBean extends BaseResponseBean {
    Integer playerCount;
    Long durationInMinutes;
    String title;
    String startDate;
    GetUserResponseBean contactUser;
    GetStadiumResponseBean stadium;
    Double costPerPlayer;
}