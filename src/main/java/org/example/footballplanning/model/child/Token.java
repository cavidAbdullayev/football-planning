package org.example.footballplanning.model.child;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.enums.TokenTypeEnum;
import org.example.footballplanning.model.parent.BaseEnt;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name = "token")
public class Token extends BaseEnt implements Serializable {
    String strToken;

    LocalDateTime expireTime;

    @Builder.Default
    boolean isUsed=false;

    @Enumerated(EnumType.STRING)
    TokenTypeEnum usedFor;

    @ManyToOne
    UserEnt user;

    @Override
    public String toString() {
        return "Token{" +
                "strToken='" + strToken + '\'' +
                ", expireTime=" + expireTime +
                '}';
    }
}
