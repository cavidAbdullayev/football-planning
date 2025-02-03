package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.feedback.createFeedback.CreateFeedbackRequestBean;
import org.example.footballplanning.bean.feedback.createFeedback.CreateFeedbackResponseBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.FeedbackResponseBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.GetFeedbacksByUsernameRequestBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.GetFeedbacksByUsernameResponseBean;
import org.example.footballplanning.model.child.FeedbackEnt;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.FeedBackRepository;
import org.example.footballplanning.repository.UserRepository;
import org.example.footballplanning.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.example.footballplanning.helper.GeneralHelper.*;
@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    UserRepository userRepository;
    FeedBackRepository feedBackRepository;

    @Override
    public CreateFeedbackResponseBean createFeedback(CreateFeedbackRequestBean request) {
        CreateFeedbackResponseBean response = new CreateFeedbackResponseBean();
        validateFields(request);
        String username = request.getUsername();
        UserEnt user = userRepository.findByUsernameAndState(username, 1).orElseThrow(() -> new RuntimeException("User doesn't exists!"));
        FeedbackEnt feedback = mapFields(new FeedbackEnt(), request);
        feedback.setUser(user);
        feedBackRepository.save(feedback);
        response.setTimeStamp(dateTimeToStr(feedback.getCreatedDate()));
        mapFields(response, request);
        return createResponse(response, "Feedback sent successfully!");
    }

    @Override
    public GetFeedbacksByUsernameResponseBean getByFeedbacksUsername(GetFeedbacksByUsernameRequestBean request) {
        GetFeedbacksByUsernameResponseBean response = new GetFeedbacksByUsernameResponseBean();
        List<FeedbackResponseBean> feedbackResponses = new ArrayList<>();
        validateFields(request);
        String username = request.getUsername();
        UserEnt user = userRepository.findByUsernameAndState(username, 1).orElseThrow(() -> new RuntimeException("User don't exists!"));
        List<FeedbackEnt> feedbacks = user.getSentFeedbacks();
        for (FeedbackEnt feedback : feedbacks) {
            FeedbackResponseBean feedbackResponse = new FeedbackResponseBean();
            mapFields(feedbackResponse, feedback);
            feedbackResponse.setTimeStamp(dateTimeToStr(feedback.getCreatedDate()));
            feedbackResponses.add(feedbackResponse);
        }
        response.setFeedbacks(feedbackResponses);
        mapFields(response, request);
        return response;
    }
}