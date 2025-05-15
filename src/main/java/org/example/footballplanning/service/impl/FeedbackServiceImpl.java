package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.feedback.createFeedback.CreateFeedbackRequestBean;
import org.example.footballplanning.bean.feedback.createFeedback.CreateFeedbackResponseBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.FeedbackResponseBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.GetFeedbacksUsernameRequestBean;
import org.example.footballplanning.bean.feedback.getFeedbacks.GetFeedbacksUsernameResponseBean;
import org.example.footballplanning.helper.FeedbackServiceHelper;
import org.example.footballplanning.helper.UserServiceHelper;
import org.example.footballplanning.model.child.FeedbackEnt;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.FeedbackRepository;
import org.example.footballplanning.service.FeedbackService;
import org.example.footballplanning.specifications.FeedbackSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.footballplanning.staticData.GeneralStaticData.currentUserId;
import static org.example.footballplanning.util.GeneralUtil.*;
import static org.example.footballplanning.util.ValidationUtil.checkPageSizeAndNumber;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    FeedbackServiceHelper feedbackServiceHelper;
    UserServiceHelper userServiceHelper;
    FeedbackRepository feedbackRepository;

    @Override
    public CreateFeedbackResponseBean createFeedback(CreateFeedbackRequestBean request) {
        validateFields(request);

        UserEnt user = userServiceHelper.getUserById(currentUserId);

        FeedbackEnt feedback = mapFields(new FeedbackEnt(), request);
        feedback.setUser(user);

        feedbackServiceHelper.save(feedback);

        CreateFeedbackResponseBean response = new CreateFeedbackResponseBean();

        response.setTimeStamp(dateTimeToStr(feedback.getCreatedDate()));

        mapFields(response, request);

        return createResponse(response, "Feedback sent successfully!");
    }

    @Override
    public GetFeedbacksUsernameResponseBean getByFeedbacksUsername(GetFeedbacksUsernameRequestBean request) {

        userServiceHelper.getUserById(currentUserId);

        request.normalize();

        Integer page = request.getPage();
        Integer size = request.getSize();

        checkPageSizeAndNumber(size, page);

        Pageable pageable = PageRequest.of(page, size);

        Specification<FeedbackEnt> specification = Specification
                .where(FeedbackSpecifications.hasUserId(currentUserId));

        if (request.getDate() != null && !request.getDate().isEmpty()) {
            specification.and(FeedbackSpecifications.afterDate(strToDateTime(request.getDate())));
        }

        List<FeedbackEnt> feedbacks = feedbackRepository.findAll(specification, pageable)
                .toList();

        List<FeedbackResponseBean> feedbackResponses = feedbacks.stream()
                .map(feedback -> {
                    FeedbackResponseBean feedbackResponse = new FeedbackResponseBean();
                    mapFields(feedbackResponse, feedback);
                    return feedbackResponse;
                }).toList();

        return GetFeedbacksUsernameResponseBean.builder()
                .message("Your feedbacks:")
                .feedbacks(feedbackResponses)
                .build();
    }
}