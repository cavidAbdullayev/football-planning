package org.example.footballplanning.service;

import org.example.footballplanning.bean.feedback.createFeedback.CreateFeedbackRequestBean;
import org.example.footballplanning.bean.feedback.createFeedback.CreateFeedbackResponseBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.GetFeedbacksByUsernameRequestBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.GetFeedbacksByUsernameResponseBean;


public interface FeedbackService {
    CreateFeedbackResponseBean createFeedback(CreateFeedbackRequestBean request);
    GetFeedbacksByUsernameResponseBean getByFeedbacksUsername(GetFeedbacksByUsernameRequestBean request);
}
