package org.example.footballplanning.bean.stadium.get;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class GetStadiumResponseBean extends BaseResponseBean {
    String name;
    String location;
    Double hourlyRate;
}
