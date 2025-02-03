package org.example.footballplanning.bean.announcement.get;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.footballplanning.bean.base.BaseResponseBean;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class GetAnnouncementResponse extends BaseResponseBean {
    Integer playerCount;
    Long durationInMinutes;
    Double costPerPlayer;
    String title;
    String startDate;
    String endDate;
}