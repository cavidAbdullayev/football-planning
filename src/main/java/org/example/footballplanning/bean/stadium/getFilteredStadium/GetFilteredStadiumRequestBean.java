package org.example.footballplanning.bean.stadium.getFilteredStadium;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetFilteredStadiumRequestBean {
    String name;
    String location;
    Double minHourlyRate;
    Double maxHourlyRate;
    Integer page;
    Integer size;

    public void normalize() {
        if (this.page == null) this.page = 0;
        if (this.size == null || this.page == 0) this.size = 5;
    }
}
