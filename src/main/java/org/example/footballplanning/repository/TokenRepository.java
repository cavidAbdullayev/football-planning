package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,String> {
    Optional<Token>findByStrTokenAndState(String strToken,Integer state);
}
