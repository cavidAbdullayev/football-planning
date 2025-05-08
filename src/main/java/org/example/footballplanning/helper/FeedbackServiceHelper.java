package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.child.FeedbackEnt;
import org.example.footballplanning.repository.FeedbackRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class FeedbackServiceHelper {
    FeedbackRepository feedBackRepository;

    @CachePut(value = "feedbackCache", key = "#result.id")
    public FeedbackEnt save(FeedbackEnt feedback) {
        return feedBackRepository.save(feedback);
    }
}