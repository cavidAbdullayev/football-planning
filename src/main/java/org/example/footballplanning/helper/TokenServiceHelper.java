package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.enums.TokenTypeEnum;
import org.example.footballplanning.exception.customExceptions.ObjectAlreadyExistsException;
import org.example.footballplanning.exception.customExceptions.ObjectNotFoundException;
import org.example.footballplanning.exception.customExceptions.SessionExpiredException;
import org.example.footballplanning.model.child.Token;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class TokenServiceHelper {
    final TokenRepository tokenRepository;

    @Value("${app.token.expire-time}")
    long tokenExpireTime;

    public Token generateToken(UserEnt user, TokenTypeEnum tokenType) {
        return Token.builder()
                .strToken(UUID.randomUUID().toString())
                .usedFor(tokenType)
                .expireTime(LocalDateTime.now().plusMinutes(tokenExpireTime))
                .user(user)
                .build();
    }

    public Token getOrGenerateToken(UserEnt user, TokenTypeEnum tokenType) {
        return Optional.ofNullable(user.getTokens())
                .flatMap(tokens -> tokens.stream()
                        .filter(token -> token.getUsedFor().equals(tokenType))
                        .findFirst()
                        .map(this::updateToken))
                .orElseGet(() -> generateToken(user, tokenType));
    }

    private Token updateToken(Token token) {
        token.setStrToken(UUID.randomUUID().toString());
        token.setExpireTime(LocalDateTime.now().plusMinutes(tokenExpireTime));
        return token;
    }

    public Token checkAndGetToken(String strToken) {
        Token token = tokenRepository.findByStrTokenAndState(strToken, 1).orElse(null);
        if (isNull(token)) {
            throw new ObjectNotFoundException("Token not found!");
        }
        if (token.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new SessionExpiredException("Token has been expired!");
        }
        return token;
    }
}