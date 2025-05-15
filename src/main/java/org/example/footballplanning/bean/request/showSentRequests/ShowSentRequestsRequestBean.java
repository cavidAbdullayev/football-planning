package org.example.footballplanning.bean.request.showSentRequests;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ShowSentRequestsRequestBean {
    Integer size;
    Integer page;
    @Nullable
    String date;
    @Nullable
    String toUserId;

    public void normalize() {
        if (this.page == null) this.page = 0;
        if (this.size == null || this.page == 0) this.size = 5;
    }
}