package org.example.footballplanning.bean.feedback.getFeedbacks;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetFeedbacksByUsernameResponseBean extends BaseResponseBean {
    String username;
    List<FeedbackResponseBean> feedbacks;
}