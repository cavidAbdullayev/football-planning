package org.example.footballplanning.bean.stadium.create;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateStadiumResponseBean extends BaseResponseBean {
    String name;
    String location;
    Double hourlyRate;
}
