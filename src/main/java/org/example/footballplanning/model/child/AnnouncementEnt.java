package org.example.footballplanning.model.child;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.parent.BaseEnt;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "announcement")
public class AnnouncementEnt extends BaseEnt {
    boolean isActive;
    @Max(22)
    @Min(8)
    Integer playerCount;
    @Max(120)
    @Min(30)
    Long durationInMinutes;
    Double costPerPlayer;
    @Column(length = 80)
    String title;

    LocalDateTime matchDay;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "announcement")
    List<RequestEnt>requests;
    @ManyToOne
    UserEnt contactUser;
    @ManyToOne
    StadiumEnt stadium;
    @ManyToOne
    TeamEnt team;
    @ManyToOne
    UserEnt user;
    @Override
    public String toString() {
        return "AnnouncementEnt{" +
                "isActive=" + isActive +
                ", playerCount=" + playerCount +
                ", durationInMinutes=" + durationInMinutes +
                ", costPerPlayer=" + costPerPlayer +
                ", title='" + title + '\'' +
                ", matchDay=" + matchDay +
                '}';
    }
}
