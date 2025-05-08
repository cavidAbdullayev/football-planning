package org.example.footballplanning.bean.match.get;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.footballplanning.bean.base.BaseResponseBean;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
public class GetMatchResponseBean extends BaseResponseBean {
    Long durationInMinutes;
    Double costPerPlayer;
    String matchDate;
    String announcementId;
}