package org.example.footballplanning.model.child;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.parent.BaseEnt;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "photo")
public class PhotoEnt extends BaseEnt {
    String path;
    String format;

    @OneToOne
    @JoinColumn(name = "user_id")
    UserEnt user;

    @Override
    public String toString() {
        return "PhotoEnt{" +
                ", path='" + path + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}
