package org.example.footballplanning.model.child;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.enums.PaymentMethodEnum;
import org.example.footballplanning.model.parent.BaseEnt;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "payment")
public class PaymentEnt extends BaseEnt {
    Double amount;

    @CreationTimestamp
    LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    PaymentMethodEnum paymentMethod;

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEnt user;

    @Override
    public String toString() {
        return "PaymentEnt{" +
                "amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentMethod=" + paymentMethod +
                '}';
    }
}
