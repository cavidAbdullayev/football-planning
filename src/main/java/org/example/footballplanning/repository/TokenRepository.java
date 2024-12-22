package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,String> {
    Optional<Token>findByStrTokenAndState(String strToken,Integer state);
}
