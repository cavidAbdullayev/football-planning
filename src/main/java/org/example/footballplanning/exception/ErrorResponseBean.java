package org.example.footballplanning.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.springframework.http.HttpStatus;
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ErrorResponseBean extends BaseResponseBean {
    HttpStatus status;
    Object data;
}
