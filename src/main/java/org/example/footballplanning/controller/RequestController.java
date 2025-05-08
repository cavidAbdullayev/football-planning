package org.example.footballplanning.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementResponseBean;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.example.footballplanning.bean.request.get.GetRequestResponseBean;
import org.example.footballplanning.bean.request.rollbackRequest.RollbackRequestRequestBean;
import org.example.footballplanning.bean.request.showReceivedRequests.ShowReceivedRequestsRequestBean;
import org.example.footballplanning.bean.request.showSentRequests.ShowSentRequestsRequestBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestRequestBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestResponseBean;
import org.example.footballplanning.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

@RestController
@RequestMapping("/request")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RequestController implements Serializable {
    RequestService requestService;

    @PostMapping("/send-request")
    public ResponseEntity<RequestToAnnouncementResponseBean> sendRequest(
            @RequestBody @Valid RequestToAnnouncementRequestBean request) {
        RequestToAnnouncementResponseBean response = requestService.sendRequestToAnnouncement(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/receive-request")
    public ResponseEntity<ReceiveRequestResponseBean> receiveRequest(
            @RequestBody @Valid ReceiveRequestRequestBean request) {
        ReceiveRequestResponseBean response = requestService.receiveRequest(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/show-sent-requests")
    public ResponseEntity<List<GetRequestResponseBean>> showSentRequests(@RequestBody ShowSentRequestsRequestBean request) {
        List<GetRequestResponseBean> response = requestService.showSentRequests(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/show-received-requests")
    public ResponseEntity<List<GetRequestResponseBean>> showReceivedRequests(@RequestBody ShowReceivedRequestsRequestBean request) {
        List<GetRequestResponseBean> response = requestService.showReceivedRequests(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/rollback-request")
    public ResponseEntity<BaseResponseBean> rollbackRequest(@RequestBody RollbackRequestRequestBean request) {
        BaseResponseBean response = requestService.rollbackRequest(request);
        return ResponseEntity.ok(response);
    }
}
