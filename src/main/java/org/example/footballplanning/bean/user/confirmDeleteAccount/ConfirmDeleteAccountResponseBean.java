package org.example.footballplanning.bean.user.confirmDeleteAccount;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmDeleteAccountResponseBean extends BaseResponseBean {
    String username;
}
