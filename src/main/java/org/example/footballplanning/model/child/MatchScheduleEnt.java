package org.example.footballplanning.model.child;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.parent.BaseEnt;

import java.time.LocalDateTime;
@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "match_schedule")
public class MatchScheduleEnt extends BaseEnt {
    LocalDateTime startDate;
    LocalDateTime endDAte;

    @OneToOne
    MatchEnt match;

    @Override
    public String toString() {
        return "MatchScheduleEnt{" +
                "startDate=" + startDate +
                ", endDAte=" + endDAte +
                '}';
    }
}
