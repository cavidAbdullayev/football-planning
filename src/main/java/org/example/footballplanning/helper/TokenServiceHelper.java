package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.example.footballplanning.bean.user.confirmRegister.ConfirmRegisterResponseBean;
import org.example.footballplanning.enums.TokenType;
import org.example.footballplanning.model.child.Token;
import org.example.footballplanning.model.child.UserEnt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Component
public class TokenServiceHelper {
    public Token generateToken(UserEnt user, TokenType tokenType){
        return Token.builder()
                .strToken(UUID.randomUUID().toString())
                .usedFor(tokenType)
                .expireTime(LocalDateTime.now().plusSeconds(30))
                .user(user)
                .build();
    }
    public String checkToken(Token token){
        String message=null;
        if(Objects.isNull(token)) {
            message="Token not found!";
        }
        if(token.getExpireTime().isAfter(LocalDateTime.now())){
            message="Token has been expired!";
        }
        return message;
    }
}
