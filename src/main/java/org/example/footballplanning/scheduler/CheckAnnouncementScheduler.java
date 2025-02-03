package org.example.footballplanning.scheduler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.child.AnnouncementEnt;
import org.example.footballplanning.model.child.RequestEnt;
import org.example.footballplanning.repository.AnnouncementRepository;
import org.example.footballplanning.repository.RequestRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class CheckAnnouncementScheduler {
    AnnouncementRepository announcementRepository;
    RequestRepository requestRepository;

    @Scheduled(cron = "*/3 * * * * ?")
    public void checkAnnouncementRequests() {
        LocalDateTime now = LocalDateTime.now();
        List<AnnouncementEnt> announcements = announcementRepository.findAllByStartDateIsBeforeAndActiveOrAfterAndDeactivated(now);
        System.out.println("Found Announcements: " + announcements.size());
        announcements.forEach(announcement -> {
            List<RequestEnt> requests = announcement.getRequests();
            requests.forEach(request -> request.setState(0));
            announcement.setState(0);
            requestRepository.saveAll(requests);
            System.out.println("Requests updated: " + requests.size());
            announcementRepository.save(announcement);
            System.out.println("Announcement updated: " + announcement.getId());
        });
    }
}