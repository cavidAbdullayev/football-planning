package org.example.footballplanning.model.child;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.parent.BaseEnt;

import java.time.LocalDateTime;
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "match_")
public class MatchEnt extends BaseEnt {
    @Max(120)
    @Min(30)
    Long durationInMinutes;
    Double costPerPlayer;

    LocalDateTime matchDate;

    @ManyToOne
    TeamEnt teamA;
    @ManyToOne
    TeamEnt teamB;
    @ManyToOne
    StadiumEnt stadium;

    @Override
    public String toString() {
        return "MatchEnt{" +
                "durationInMinutes=" + durationInMinutes +
                ", costPerPlayer=" + costPerPlayer +
                ", matchDate=" + matchDate +
                '}';
    }
}
