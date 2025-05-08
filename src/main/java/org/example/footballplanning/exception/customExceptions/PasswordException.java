package org.example.footballplanning.exception.customExceptions;

public class PasswordException extends RuntimeException{
    public PasswordException(String message){
        super(message);
    }
}
