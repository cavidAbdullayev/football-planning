//package org.example.footballplanning.scheduler;
//
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.example.footballplanning.helper.AnnouncementServiceHelper;
//import org.example.footballplanning.helper.RequestServiceHelper;
//import org.example.footballplanning.model.child.AnnouncementEnt;
//import org.example.footballplanning.model.child.RequestEnt;
//import org.example.footballplanning.repository.AnnouncementRepository;
//import org.example.footballplanning.repository.RequestRepository;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
//@RequiredArgsConstructor
//public class CheckAnnouncementScheduler {
//    AnnouncementRepository announcementRepository;
//    RequestServiceHelper requestServiceHelper;
//    AnnouncementServiceHelper announcementServiceHelper;
//    RequestRepository requestRepository;
//
//    @Scheduled(cron = "*/3 * * * * ?")
//    public void checkAnnouncementRequests() {
//        LocalDateTime now = LocalDateTime.now();
//        List<AnnouncementEnt> announcements = announcementRepository.findAllByStartDateIsBeforeAndActiveOrAfterAndDeactivated(now);
//        announcements.forEach(announcement -> {
//            List<RequestEnt> requests = announcement.getRequests()
//                    .stream()
//                    .peek(requestEnt -> requestEnt.setState(0))
//                    .toList();
//            requestServiceHelper.saveAll(requests);
//            announcement.setState(0);
//            announcementServiceHelper.saveOrUpdate(announcement);
//            System.out.println("Announcement updated: " + announcement.getId());
//        });
//    }
//}