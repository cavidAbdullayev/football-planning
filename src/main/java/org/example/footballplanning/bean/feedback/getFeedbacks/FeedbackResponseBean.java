package org.example.footballplanning.bean.feedback.getFeedbacks;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackResponseBean extends BaseResponseBean {
    String content;
    String timeStamp;
}