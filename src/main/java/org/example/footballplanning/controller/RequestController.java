package org.example.footballplanning.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementResponseBean;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.example.footballplanning.bean.request.get.GetRequestResponseBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestRequestBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestResponseBean;
import org.example.footballplanning.service.RequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/request")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class RequestController {
    RequestService requestService;

    @PostMapping("/send-request")
    public RequestToAnnouncementResponseBean sendRequest(@RequestBody @Valid RequestToAnnouncementRequestBean request) {
        return requestService.sendRequestToAnnouncement(request);
    }

    @PostMapping("/receive-request")
    public ReceiveRequestResponseBean receiveRequest(@RequestBody @Valid ReceiveRequestRequestBean request) {
        return requestService.receiveRequest(request);
    }

    @GetMapping("/show-sent-requests")
    List<GetRequestResponseBean> showSentRequests() {
        return requestService.showSentRequests();
    }

    @GetMapping("/show-received-requests")
    List<GetRequestResponseBean> showReceivedRequests() {
        return requestService.showReceivedRequests();
    }

    @PutMapping("/rollback-request/{requestId}")
    public BaseResponseBean rollbackRequest(@PathVariable("requestId") String requestId) {
        return requestService.rollbackRequest(requestId);
    }
}