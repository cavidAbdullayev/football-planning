package org.example.footballplanning.exception.customExceptions;

public class SessionExpiredException extends RuntimeException{
    public SessionExpiredException(String message){
        super(message);
    }
}
