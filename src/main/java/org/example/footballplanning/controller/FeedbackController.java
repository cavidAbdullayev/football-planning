package org.example.footballplanning.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.feedback.createFeedback.CreateFeedbackRequestBean;
import org.example.footballplanning.bean.feedback.createFeedback.CreateFeedbackResponseBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.GetFeedbacksUsernameRequestBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.GetFeedbacksUsernameResponseBean;
import org.example.footballplanning.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class FeedbackController {
    FeedbackService feedbackService;

    @PostMapping("/create-feedback")
    public ResponseEntity<CreateFeedbackResponseBean> createFeedback(@RequestBody @Valid CreateFeedbackRequestBean request) {
        return ResponseEntity.ok(feedbackService.createFeedback(request));
    }

    @GetMapping("/get-all-feedbacks-by-username")
    public ResponseEntity<GetFeedbacksUsernameResponseBean> getByFeedbacksUsername(@RequestBody @Valid GetFeedbacksUsernameRequestBean request) {
        return ResponseEntity.ok(feedbackService.getByFeedbacksUsername(request));
    }
}