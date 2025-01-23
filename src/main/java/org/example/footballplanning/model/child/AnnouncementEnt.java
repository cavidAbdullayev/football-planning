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
    @Builder.Default
    boolean isActive=true;
    @Max(value = 22,message = "Player count must be less than 22!")
    @Min(value = 8,message = "Player count must be greater than 8!")
    Integer playerCount;
    @Max(value = 120,message = "Duration in minutes must be less than 120!")
    @Min(value = 30,message = "Duration in minutes must be greater than 30!")
    Long durationInMinutes;
    Double costPerPlayer;
    @Column(length = 80)
    String title;

    LocalDateTime startDate;
    LocalDateTime endDate;



    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "announcement")
    List<RequestEnt>requests;
    @ManyToOne
    @JoinColumn(name = "contact_user_id",nullable = false,referencedColumnName = "id")
    UserEnt contactUser;
    @ManyToOne
    StadiumEnt stadium;
    @ManyToOne
    TeamEnt team;

    @Override
    public String toString() {
        return "AnnouncementEnt{" +
                "isActive=" + isActive +
                ", playerCount=" + playerCount +
                ", durationInMinutes=" + durationInMinutes +
                ", costPerPlayer=" + costPerPlayer +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", requests=" + requests +
                ", contactUser=" + contactUser +
                ", stadium=" + stadium +
                ", team=" + team +
                '}';
    }
}
