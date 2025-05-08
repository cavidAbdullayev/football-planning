package org.example.footballplanning.exception.customExceptions;

public class PhotoOperationException extends RuntimeException{
    public PhotoOperationException(String message){
        super(message);
    }
}
