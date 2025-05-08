package org.example.footballplanning.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.announcement.activateOldAnnouncement.ActivateOldAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.activateOldAnnouncement.ActivateOldAnnouncementResponseBean;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementResponseBean;
import org.example.footballplanning.bean.announcement.deactivateAnnouncement.DeactivateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.deactivateAnnouncement.DeactivateAnnouncementResponseBean;
import org.example.footballplanning.bean.announcement.get.GetAnnouncementResponse;
import org.example.footballplanning.bean.announcement.showMyAnnouncements.ShowMyAnnouncementsRequestBean;
import org.example.footballplanning.bean.announcement.update.UpdateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.update.UpdateAnnouncementResponseBean;
import org.example.footballplanning.service.AnnouncementService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/announcement")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class AnnouncementController {
    AnnouncementService announcementService;

    @PostMapping("/create-announcement")
    public CreateAnnouncementResponseBean createAnnouncement(@RequestBody @Valid CreateAnnouncementRequestBean request) {
        return announcementService.createAnnouncement(request);
    }

    @DeleteMapping("/deactivate-announcement")
    public DeactivateAnnouncementResponseBean deactivateAnnouncement(@RequestBody DeactivateAnnouncementRequestBean request) {
        return announcementService.deactivateAnnouncement(request);
    }

    @PutMapping("/update-announcement")
    public UpdateAnnouncementResponseBean updateAnnouncement(@RequestBody UpdateAnnouncementRequestBean request) {
        return announcementService.updateAnnouncement(request);
    }

    @PutMapping("/activate-old-announcement")
    public ActivateOldAnnouncementResponseBean activateOldAnnouncement(ActivateOldAnnouncementRequestBean request) {
        return announcementService.activateOldAnnouncement(request);
    }

    @GetMapping("/show-my-announcements")
    public List<GetAnnouncementResponse> showMyAnnouncements(@RequestBody ShowMyAnnouncementsRequestBean request) {
        return announcementService.showMyAnnouncements(request);
    }

}