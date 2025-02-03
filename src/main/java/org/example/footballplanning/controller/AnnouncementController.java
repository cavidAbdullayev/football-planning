package org.example.footballplanning.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementResponseBean;
import org.example.footballplanning.bean.announcement.update.UpdateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.update.UpdateAnnouncementResponseBean;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.example.footballplanning.service.AnnouncementService;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/deactivate-announcement/{announcementId}")
    public BaseResponseBean deactivateAnnouncement(@PathVariable String announcementId) {
        return announcementService.deactivateAnnouncement(announcementId);
    }

    @PutMapping("/update-announcement")
    public UpdateAnnouncementResponseBean updateAnnouncement(@RequestBody UpdateAnnouncementRequestBean request) {
        return announcementService.updateAnnouncement(request);
    }
}