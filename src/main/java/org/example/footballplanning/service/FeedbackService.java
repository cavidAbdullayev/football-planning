package org.example.footballplanning.service;

import org.example.footballplanning.bean.feedback.createFeedback.CreateFeedbackRequestBean;
import org.example.footballplanning.bean.feedback.createFeedback.CreateFeedbackResponseBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.GetFeedbacksUsernameRequestBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.GetFeedbacksUsernameResponseBean;


public interface FeedbackService {
    CreateFeedbackResponseBean createFeedback(CreateFeedbackRequestBean request);

    GetFeedbacksUsernameResponseBean getByFeedbacksUsername(GetFeedbacksUsernameRequestBean request);
}