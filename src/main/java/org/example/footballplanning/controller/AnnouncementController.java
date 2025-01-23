package org.example.footballplanning.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementResponseBean;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementResponseBean;
import org.example.footballplanning.service.AnnouncementService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/announcement")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class AnnouncementController {
    AnnouncementService announcementService;
    @PostMapping("/create-announcement")
    public CreateAnnouncementResponseBean createAnnouncement(@RequestBody @Valid CreateAnnouncementRequestBean request){
        return announcementService.createAnnouncement(request);
    }

    @PostMapping("/sent-request")
    public RequestToAnnouncementResponseBean sendRequest(@RequestBody @Valid RequestToAnnouncementRequestBean request){
        return announcementService.sendRequestToAnnouncement(request);
    }

}
