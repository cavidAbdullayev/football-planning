package org.example.footballplanning.model.child;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.parent.BaseEnt;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "request")
public class RequestEnt extends BaseEnt implements Serializable {
    @Column(length = 80)
    String message;
    Integer playerCount;

    @ManyToOne
    UserEnt from;
    @ManyToOne
    UserEnt to;
    @ManyToOne
    AnnouncementEnt announcement;
    @ManyToOne
    TeamEnt team;

    @Override
    public String toString() {
        return "RequestEnt{" +
                "message='" + message + '\'' +
                '}';
    }
}
