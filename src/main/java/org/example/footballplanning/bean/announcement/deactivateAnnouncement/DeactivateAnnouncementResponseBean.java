package org.example.footballplanning.bean.announcement.deactivateAnnouncement;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.footballplanning.bean.base.BaseResponseBean;
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class DeactivateAnnouncementResponseBean extends BaseResponseBean {
    String ID;
}
