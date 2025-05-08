package org.example.footballplanning.exception.customExceptions;

public class ObjectAlreadyExistsException extends RuntimeException{
    public ObjectAlreadyExistsException(String message){
        super(message);
    }
}
