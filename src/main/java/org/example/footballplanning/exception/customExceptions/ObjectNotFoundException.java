package org.example.footballplanning.exception.customExceptions;

public class ObjectNotFoundException extends RuntimeException{
    public ObjectNotFoundException(String message){
        super(message);
    }
}
