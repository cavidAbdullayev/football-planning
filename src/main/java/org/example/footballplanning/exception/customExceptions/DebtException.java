package org.example.footballplanning.exception.customExceptions;

public class DebtException extends RuntimeException{
    public DebtException(String message){
        super(message);
    }
}
