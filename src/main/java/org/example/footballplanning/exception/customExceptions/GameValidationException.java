package org.example.footballplanning.exception.customExceptions;

public class GameValidationException extends RuntimeException{
    public GameValidationException(String message){
        super(message);
    }
}
