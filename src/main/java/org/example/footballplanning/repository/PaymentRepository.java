package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.PaymentEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEnt,String> {
}
