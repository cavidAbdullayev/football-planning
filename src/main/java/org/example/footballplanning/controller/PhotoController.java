package org.example.footballplanning.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.photo.deletePhoto.DeletePhotoResponseBean;
import org.example.footballplanning.bean.photo.uploadPhoto.UploadPhotoResponseBean;
import org.example.footballplanning.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PhotoController {
    PhotoService photoService;

    @PostMapping("/upload-photo")
    public ResponseEntity<UploadPhotoResponseBean> uploadPhoto(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(photoService.uploadPhoto(file));
    }

    @DeleteMapping("/delete-photo")
    public ResponseEntity<DeletePhotoResponseBean> deletePhoto() {
        return ResponseEntity.ok(photoService.deletePhoto());
    }

    @GetMapping("/show-profile-photo")
    public ResponseEntity<byte[]> showProfilePhoto() {
        return photoService.showProfilePhoto();
    }
}