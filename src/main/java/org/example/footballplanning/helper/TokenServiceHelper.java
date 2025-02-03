package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.enums.TokenTypeEnum;
import org.example.footballplanning.model.child.Token;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.TokenRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Component
public class TokenServiceHelper {
    TokenRepository tokenRepository;

    public Token generateToken(UserEnt user, TokenTypeEnum tokenType) {
        return Token.builder()
                .strToken(UUID.randomUUID().toString())
                .usedFor(tokenType)
                .expireTime(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();
    }

    public Token getOrGenerateToken(UserEnt user, TokenTypeEnum tokenType) {
        return user.getTokens().stream()
                .filter(t -> t.getUsedFor().equals(tokenType))
                .findFirst()
                .map(this::updateToken)
                .orElseGet(() -> generateToken(user, tokenType));
    }

    private Token updateToken(Token token) {
        token.setStrToken(UUID.randomUUID().toString());
        token.setExpireTime(LocalDateTime.now().plusMinutes(5));
        return token;
    }

    public Token checkAndGetToken(String strToken) {
        Token token = tokenRepository.findByStrTokenAndState(strToken, 1).orElse(null);
        if (isNull(token)) {
            throw new RuntimeException("Token not found!");
        }
        if (token.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has been expired!");
        }
        return token;
    }
}