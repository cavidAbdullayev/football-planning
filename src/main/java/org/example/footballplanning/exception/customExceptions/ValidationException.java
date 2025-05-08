package org.example.footballplanning.exception.customExceptions;

public class ValidationException extends RuntimeException{
    public ValidationException(String message){
        super(message);
    }
}
