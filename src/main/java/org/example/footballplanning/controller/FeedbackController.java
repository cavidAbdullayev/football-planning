package org.example.footballplanning.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.feedback.createFeedback.CreateFeedbackRequestBean;
import org.example.footballplanning.bean.feedback.createFeedback.CreateFeedbackResponseBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.GetFeedbacksByUsernameRequestBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.GetFeedbacksByUsernameResponseBean;
import org.example.footballplanning.service.FeedbackService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class FeedbackController {
    FeedbackService feedbackService;

    @PostMapping("/create-feedback")
    public CreateFeedbackResponseBean createFeedback(@RequestBody CreateFeedbackRequestBean request) {
        return feedbackService.createFeedback(request);
    }

    @GetMapping("/get-all-feedbacks-by-username")
    public GetFeedbacksByUsernameResponseBean getByFeedbacksUsername(@RequestBody GetFeedbacksByUsernameRequestBean request) {
        return feedbackService.getByFeedbacksUsername(request);
    }
}