package org.example.footballplanning.bean.feedback.createFeedback;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFeedbackResponseBean extends BaseResponseBean {
    String username;
    String content;
    String timeStamp;
}