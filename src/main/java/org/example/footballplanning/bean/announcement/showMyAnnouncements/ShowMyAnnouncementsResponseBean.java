package org.example.footballplanning.bean.announcement.showMyAnnouncements;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.footballplanning.bean.announcement.get.GetAnnouncementResponse;
import org.example.footballplanning.bean.base.BaseResponseBean;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
public class ShowMyAnnouncementsResponseBean extends BaseResponseBean {
    List<GetAnnouncementResponse>announcements;
}
