package org.example.footballplanning.service;

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

import java.util.List;

public interface AnnouncementService {
    CreateAnnouncementResponseBean createAnnouncement(CreateAnnouncementRequestBean request);

    DeactivateAnnouncementResponseBean deactivateAnnouncement(DeactivateAnnouncementRequestBean request);

    UpdateAnnouncementResponseBean updateAnnouncement(UpdateAnnouncementRequestBean request);

    ActivateOldAnnouncementResponseBean activateOldAnnouncement(ActivateOldAnnouncementRequestBean request);

    public List<GetAnnouncementResponse> showMyAnnouncements(ShowMyAnnouncementsRequestBean request);
}