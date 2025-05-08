package org.example.footballplanning.bean.feedback.getFeedbacks;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetFeedbacksUsernameRequestBean {
    @Nullable
    String date;
    Integer size;
    Integer page;

    public void normalize() {
        if (this.page == null) this.page = 0;
        if (this.size == null) this.size = 5;
    }
}