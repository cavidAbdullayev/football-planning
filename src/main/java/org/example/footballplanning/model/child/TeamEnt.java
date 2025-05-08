package org.example.footballplanning.model.child;

import jakarta.persistence.*;
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
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Table(name = "team")
public class TeamEnt extends BaseEnt implements Serializable {
    @Column(length = 30)
    String teamName;

    @OneToOne
    UserEnt managerUser;

    @OneToMany(mappedBy = "team",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    List<AnnouncementEnt>announcements;

    @OneToMany(mappedBy = "teamA")
    List<MatchEnt>homeMatch;

    @OneToMany(mappedBy = "teamB")
    List<MatchEnt>awayMatch;

    @Override
    public String toString() {
        return "TeamEnt{" +
                "name='" + teamName + '\'' +
                '}';
    }
}
