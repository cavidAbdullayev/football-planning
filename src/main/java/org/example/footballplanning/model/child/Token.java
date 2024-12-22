package org.example.footballplanning.model.child;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.enums.TokenType;
import org.example.footballplanning.model.parent.BaseEnt;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name = "token")
public class Token extends BaseEnt {
    String strToken;

    LocalDateTime expireTime;

    @Enumerated(EnumType.STRING)
    TokenType usedFor;

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
