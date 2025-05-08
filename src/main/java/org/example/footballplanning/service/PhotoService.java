package org.example.footballplanning.service;

import org.example.footballplanning.bean.photo.deletePhoto.DeletePhotoResponseBean;
import org.example.footballplanning.bean.photo.uploadPhoto.UploadPhotoResponseBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {
    UploadPhotoResponseBean uploadPhoto(MultipartFile file);

    DeletePhotoResponseBean deletePhoto();

    ResponseEntity<byte[]> showProfilePhoto();
}