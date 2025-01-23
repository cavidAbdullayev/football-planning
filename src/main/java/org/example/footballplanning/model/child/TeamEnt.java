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
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Table(name = "team")
public class TeamEnt extends BaseEnt {
    @Column(length = 30)
    String teamName;

    @OneToOne
    UserEnt managerUser;

    @OneToMany(mappedBy = "team",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    List<AnnouncementEnt>announcements;

    @Override
    public String toString() {
        return "TeamEnt{" +
                "name='" + teamName + '\'' +
                '}';
    }
}
