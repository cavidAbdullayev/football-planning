package org.example.footballplanning.bean.announcement.activateOldAnnouncement;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivateOldAnnouncementRequestBean {
    String announcementId;
    String startDate;
    Integer durationInMinutes;
}
