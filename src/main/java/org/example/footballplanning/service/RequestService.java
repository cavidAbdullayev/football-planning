package org.example.footballplanning.service;

import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementResponseBean;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.example.footballplanning.bean.request.get.GetRequestResponseBean;
import org.example.footballplanning.bean.request.rollbackRequest.RollbackRequestRequestBean;
import org.example.footballplanning.bean.request.showReceivedRequests.ShowReceivedRequestsRequestBean;
import org.example.footballplanning.bean.request.showSentRequests.ShowSentRequestsRequestBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestRequestBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestResponseBean;

import java.util.List;

public interface RequestService {
    RequestToAnnouncementResponseBean sendRequestToAnnouncement(RequestToAnnouncementRequestBean request);

    ReceiveRequestResponseBean receiveRequest(ReceiveRequestRequestBean request);

    List<GetRequestResponseBean> showSentRequests(ShowSentRequestsRequestBean request);

    List<GetRequestResponseBean> showReceivedRequests(ShowReceivedRequestsRequestBean request);

    BaseResponseBean rollbackRequest(RollbackRequestRequestBean request);
}