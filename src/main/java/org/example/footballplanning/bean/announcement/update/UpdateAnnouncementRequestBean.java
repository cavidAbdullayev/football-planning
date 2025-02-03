package org.example.footballplanning.bean.announcement.update;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAnnouncementRequestBean {
    String announcementId;
    Integer playerCount;
    Long durationInMinutes;
    String title;
    String startDate;
    String stadiumId;
}