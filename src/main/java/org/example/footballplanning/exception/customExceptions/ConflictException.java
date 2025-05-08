package org.example.footballplanning.exception.customExceptions;

public class ConflictException extends RuntimeException{
    public ConflictException(String message){
        super(message);
    }
}
