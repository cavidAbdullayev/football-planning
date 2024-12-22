package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.PaymentEnt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEnt,String> {
}
