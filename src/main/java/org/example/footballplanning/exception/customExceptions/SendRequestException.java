package org.example.footballplanning.exception.customExceptions;

public class SendRequestException extends RuntimeException{
    public SendRequestException(String message){
        super(message);
    }
}
