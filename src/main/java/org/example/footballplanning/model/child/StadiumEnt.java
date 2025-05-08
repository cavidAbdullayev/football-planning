package org.example.footballplanning.model.child;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.parent.BaseEnt;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "stadium")
public class StadiumEnt extends BaseEnt implements Serializable {
    @Column(length = 50, unique = true, nullable = false)
    String name;

    @Column(length = 50, nullable = false)
    String location;

    @Min(value = 0, message = "Hourly rate must be greater than or equal to 0")
    @Max(value = 1000, message = "Hourly rate must be less than or equal to 1000")
    @Column(nullable = false)
    Double hourlyRate;

    @OneToMany(mappedBy = "stadium", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    List<MatchEnt>matches;
    @OneToMany(mappedBy = "stadium", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<AnnouncementEnt>announcements;

    @Override
    public String toString() {
        return "StadiumEnt{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", hourlyRate=" + hourlyRate +
                ", matchesCount=" + (matches != null ? matches.size() : 0) +
                ", announcementsCount=" + (announcements != null ? announcements.size() : 0) +
                '}';
    }

}
