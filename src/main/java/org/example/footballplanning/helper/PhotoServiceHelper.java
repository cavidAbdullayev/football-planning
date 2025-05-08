package org.example.footballplanning.helper;

import org.example.footballplanning.exception.customExceptions.ValidationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;



@Component
public class PhotoServiceHelper {
    public MediaType getMediaType(String type) {
        return switch (type) {
            case "jpeg", "jpg" -> MediaType.IMAGE_JPEG;
            case "pmg" -> MediaType.IMAGE_PNG;
            default -> throw new ValidationException("Unsupported format!");
        };
    }
}