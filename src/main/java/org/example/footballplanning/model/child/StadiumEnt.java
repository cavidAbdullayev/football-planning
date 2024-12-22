package org.example.footballplanning.model.child;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.parent.BaseEnt;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "stadium")
public class StadiumEnt extends BaseEnt {
    @Column(length = 50)
    String name;
    @Column(length = 50)
    String location;
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
                '}';
    }
}
