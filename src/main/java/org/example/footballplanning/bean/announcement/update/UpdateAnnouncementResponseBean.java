package org.example.footballplanning.bean.announcement.update;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.announcement.get.GetAnnouncementResponse;
import org.example.footballplanning.bean.base.BaseResponseBean;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAnnouncementResponseBean extends BaseResponseBean {
    GetAnnouncementResponse announcementResponse;
}
