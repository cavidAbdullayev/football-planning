package org.example.footballplanning.service;

import org.example.footballplanning.bean.announcement.create.CreateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementResponseBean;
import org.example.footballplanning.bean.announcement.update.UpdateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.update.UpdateAnnouncementResponseBean;
import org.example.footballplanning.bean.base.BaseResponseBean;

public interface AnnouncementService {
    CreateAnnouncementResponseBean createAnnouncement(CreateAnnouncementRequestBean request);
    BaseResponseBean deactivateAnnouncement(String announcementId);
    UpdateAnnouncementResponseBean updateAnnouncement(UpdateAnnouncementRequestBean request);
}
