package org.example.footballplanning.model.child;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.parent.BaseEnt;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "feedback")
public class FeedbackEnt extends BaseEnt {
    @Column(length = 80)
    String content;

    @ManyToOne
    UserEnt user;

    @Override
    public String toString() {
        return "FeedbackEnt{" +
                "content='" + content + '\'' +
                '}';
    }
}