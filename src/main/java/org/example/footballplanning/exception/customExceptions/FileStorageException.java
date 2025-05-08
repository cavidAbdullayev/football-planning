package org.example.footballplanning.exception.customExceptions;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message){
        super(message);
    }
}