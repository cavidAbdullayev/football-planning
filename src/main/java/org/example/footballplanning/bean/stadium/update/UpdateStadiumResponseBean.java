package org.example.footballplanning.bean.stadium.update;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateStadiumResponseBean extends BaseResponseBean {
    String name;
    String location;
    Double hourlyRate;
}
