package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.photo.deletePhoto.DeletePhotoResponseBean;
import org.example.footballplanning.bean.photo.uploadPhoto.UploadPhotoResponseBean;
import org.example.footballplanning.exception.customExceptions.FileStorageException;
import org.example.footballplanning.exception.customExceptions.PhotoOperationException;
import org.example.footballplanning.exception.customExceptions.ValidationException;
import org.example.footballplanning.helper.PhotoServiceHelper;
import org.example.footballplanning.helper.UserServiceHelper;
import org.example.footballplanning.model.child.PhotoEnt;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.UserRepository;
import org.example.footballplanning.service.PhotoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.example.footballplanning.util.GeneralUtil.*;
import static org.example.footballplanning.staticData.GeneralStaticData.currentUserId;
import static org.example.footballplanning.staticData.UploadPhotoStaticData.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PhotoServiceImpl implements PhotoService {
    UserRepository userRepository;
    PhotoServiceHelper photoServiceHelper;
    UserServiceHelper userServiceHelper;

    @SneakyThrows
    @Override
    public UploadPhotoResponseBean uploadPhoto(MultipartFile file) {
        UploadPhotoResponseBean response = new UploadPhotoResponseBean();

        UserEnt user = userServiceHelper.getUserById(currentUserId);

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank() || !originalFileName.contains(".")) {
            throw new ValidationException("Invalid file name!");
        }

        String extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();

        if (!imageExtensions.contains(extension)) {
            throw new ValidationException("Unsupported file format!");
        }

        String photoPath = mediaPath + user.getUsername() + "_" + System.currentTimeMillis() + "." + extension;

        //Delete old photo
        Optional.ofNullable(user.getPhoto())
                .map(PhotoEnt::getPath)
                .map(File::new)
                .filter(File::exists)
                .ifPresent(fileToDelete -> {
                    if (!fileToDelete.delete()) {
                        throw new FileStorageException("Failed to delete old photo: " + fileToDelete.getPath());
                    }
                });

        //Save new photo
        try (InputStream inputStream = file.getInputStream();
             OutputStream outputStream = new FileOutputStream(photoPath)) {

            byte[] temp = new byte[8192]; //8 KB
            int bytesRead;
            while ((bytesRead = inputStream.read(temp)) != -1) {
                outputStream.write(temp, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new FileStorageException("Error saving photo to disk");
        }

        PhotoEnt photo = Optional.ofNullable(user.getPhoto())
                .orElseGet(PhotoEnt::new);

        photo.setUser(user);
        photo.setPath(photoPath);
        photo.setFormat(extension);

        user.setPhoto(photo);
        userRepository.save(user);


        return createResponse(response, "Photo uploaded successfully!");
    }

    @Override
    public DeletePhotoResponseBean deletePhoto() {
        DeletePhotoResponseBean response = new DeletePhotoResponseBean();

        UserEnt user = userServiceHelper.getUserById(currentUserId);

        PhotoEnt photo = user.getPhoto();

        if (photo == null) {
            throw new PhotoOperationException("You do not have a profile photo to delete!");
        }

        Path filePath = Paths.get(photo.getPath());

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete old photo: " + filePath);
        }

        user.setPhoto(null);
        userRepository.save(user);

        return createResponse(response, "Profile photo deleted successfully!");
    }

    @SneakyThrows
    @Override
    public ResponseEntity<byte[]> showProfilePhoto() {

        UserEnt user = userServiceHelper.getUserById(currentUserId);
        PhotoEnt photo = user.getPhoto();

        if (photo == null) {
            throw new PhotoOperationException("You have not any profile photo!");
        }

        Path photoPath = Paths.get(photo.getPath());

        try {
            byte[] byteFile;

            try (InputStream inputStream = Files.newInputStream(photoPath);
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] temp = new byte[8192]; //8 KB
                int bytesRead;
                while ((bytesRead = inputStream.read(temp)) != -1) {
                    buffer.write(temp, 0, bytesRead);
                }

                byteFile = buffer.toByteArray();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(photoServiceHelper.getMediaType(photo.getFormat()));

            return new ResponseEntity<>(byteFile, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new PhotoOperationException("Profile photo could not be read!");
        }

    }
}