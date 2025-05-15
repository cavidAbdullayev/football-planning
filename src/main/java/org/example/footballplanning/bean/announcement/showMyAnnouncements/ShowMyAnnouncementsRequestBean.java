package org.example.footballplanning.bean.announcement.showMyAnnouncements;

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowMyAnnouncementsRequestBean {

    Integer state;
    @Nullable
    String startDate;
    Integer size;
    Integer page;

    public void normalize() {
        if (this.page == null) this.page = 0;
        if (this.size == null || this.page == 0) this.size = 5;
    }
}