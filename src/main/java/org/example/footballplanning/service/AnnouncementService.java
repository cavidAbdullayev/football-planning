package org.example.footballplanning.service;

import org.example.footballplanning.bean.announcement.create.CreateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementResponseBean;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementResponseBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestRequestBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestResponseBean;

public interface AnnouncementService {
    CreateAnnouncementResponseBean createAnnouncement(CreateAnnouncementRequestBean request);
    RequestToAnnouncementResponseBean sendRequestToAnnouncement(RequestToAnnouncementRequestBean request);
    ReceiveRequestResponseBean receiveRequest(ReceiveRequestRequestBean request);

}
