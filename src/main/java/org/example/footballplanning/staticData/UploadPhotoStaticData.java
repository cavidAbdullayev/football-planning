package org.example.footballplanning.staticData;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
@Data
public class UploadPhotoStaticData {
   public static List<String> imageExtensions = Arrays.asList(
            "jpeg", "jpg", "png");
   public static final String mediaPath = "C:\\cavid\\Java Projects\\football-planning\\src\\main\\resources\\static\\data\\";

}
