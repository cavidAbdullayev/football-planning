package org.example.footballplanning.bean.announcement.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestToAnnouncementRequestBean {
    String fromUserId;
    String announcementId;
    String message;
    Integer playerCount;
}