package org.example.footballplanning.model.child;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.footballplanning.model.parent.BaseEnt;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SuperBuilder
@Table(name = "user_")
public class UserEnt extends BaseEnt implements Serializable {
    @Column(nullable = false)
    String firstName;

    @Column(nullable = false)
    String lastName;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String password;

    @Column(unique = true, nullable = false)
    String email;

    @Column(unique = true, nullable = false)
    String phoneNumber;

    @Column(nullable = false)
    LocalDate dateOfBirth;

    @Builder.Default
    Double debt = 0d;

    @Builder.Default
    boolean isActive = false;

    @Builder.Default
    boolean hasEverDeleted = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<PaymentEnt> payments;

    @OneToOne(mappedBy = "managerUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    TeamEnt team;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<FeedbackEnt> sentFeedbacks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<Token> tokens;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    PhotoEnt photo;

    @OneToMany(mappedBy = "contactUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<AnnouncementEnt> sharedAnnouncements;

    @OneToMany(mappedBy = "from", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<RequestEnt> sentRequests;

    @OneToMany(mappedBy = "to", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<RequestEnt> receivedRequests;

    @ElementCollection(fetch = FetchType.LAZY)
    Set<String>roles;
    @Override
    public String toString() {
        return "UserEnt{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", isActive=" + isActive +
                '}';
    }
}