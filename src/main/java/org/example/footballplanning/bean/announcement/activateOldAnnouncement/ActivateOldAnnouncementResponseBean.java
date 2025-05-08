package org.example.footballplanning.bean.announcement.activateOldAnnouncement;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.footballplanning.bean.announcement.get.GetAnnouncementResponse;
import org.example.footballplanning.bean.base.BaseResponseBean;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ActivateOldAnnouncementResponseBean extends BaseResponseBean {
    GetAnnouncementResponse announcementResponse;
}
