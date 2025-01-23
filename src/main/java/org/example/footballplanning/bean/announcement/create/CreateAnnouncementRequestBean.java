package org.example.footballplanning.bean.announcement.create;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAnnouncementRequestBean {
    Integer playerCount;
    Long durationInMinutes;
    String title;
    String startDate;
    String userId;
    String stadiumId;

}
