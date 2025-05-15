package org.example.footballplanning.bean.request.showReceivedRequests;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowReceivedRequestsRequestBean {
    Integer size;
    Integer page;
    @Nullable
    String date;
    @Nullable
    String fromUserId;

    public void normalize() {
        if (this.page == null) this.page = 0;
        if (this.size == null || this.page == 0) this.size = 5;
    }
}