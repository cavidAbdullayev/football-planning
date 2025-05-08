package org.example.footballplanning.bean.announcement.deactivateAnnouncement;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class DeactivateAnnouncementRequestBean {
    String announcementId;
}
