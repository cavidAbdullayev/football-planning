package org.example.footballplanning.helper;

import org.example.footballplanning.bean.announcement.get.GetAnnouncementResponse;
import org.example.footballplanning.model.child.AnnouncementEnt;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.footballplanning.helper.GeneralHelper.*;

@Component
public class AnnouncementServiceHelper {
    public String mapFromAnnouncementResponseToJson(AnnouncementEnt announcement) {
        return createJson(mapToGetResponse(announcement));
    }

    public GetAnnouncementResponse mapToGetResponse(AnnouncementEnt announcement) {
        return GetAnnouncementResponse.builder()
                .title(announcement.getTitle())
                .endDate(dateTimeToStr(announcement.getEndDate()))
                .startDate(dateTimeToStr(announcement.getStartDate()))
                .costPerPlayer(announcement.getCostPerPlayer())
                .durationInMinutes(announcement.getDurationInMinutes())
                .playerCount(announcement.getPlayerCount())
                .build();
    }

    public Optional<AnnouncementEnt> checkIsConflictWithAnnouncement(List<AnnouncementEnt> announcements, LocalDateTime startDate, LocalDateTime endDate) {
        return announcements.stream().filter(announcementEnt -> announcementEnt.getState() == 1 &&
                !(startDate.isAfter(announcementEnt.getEndDate().plusHours(6)) ||
                        endDate.isBefore(announcementEnt.getStartDate().minusHours(6)))).findFirst();
    }
}