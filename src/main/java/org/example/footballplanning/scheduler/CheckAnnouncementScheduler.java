package org.example.footballplanning.scheduler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.child.AnnouncementEnt;
import org.example.footballplanning.model.child.RequestEnt;
import org.example.footballplanning.repository.AnnouncementRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class CheckAnnouncementScheduler {
    AnnouncementRepository announcementRepository;
    @Scheduled(cron = "0 0 0 3 * ?")
    public void checkAnnouncements(){
        List<AnnouncementEnt>announcements=announcementRepository.findAll();
        announcements.stream().filter(announcement -> announcement.getStartDate().isBefore(LocalDateTime.now()))
                .forEach(announcement->{
                    List<RequestEnt>requests=announcement.getRequests();
                    requests.forEach(request -> request.setState(0));
                });
        announcementRepository.saveAll(announcements);
    }
}
