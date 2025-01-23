package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.photo.deletePhoto.DeletePhotoResponseBean;
import org.example.footballplanning.bean.photo.uploadPhoto.UploadPhotoResponseBean;
import org.example.footballplanning.helper.GeneralHelper;
import org.example.footballplanning.helper.PhotoServiceHelper;
import org.example.footballplanning.model.child.PhotoEnt;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.PhotoRepository;
import org.example.footballplanning.repository.UserRepository;
import org.example.footballplanning.service.PhotoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static org.example.footballplanning.helper.GeneralHelper.*;
import static org.example.footballplanning.staticData.GeneralStaticData.*;
import static org.example.footballplanning.staticData.UploadPhotoStaticData.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PhotoServiceImpl implements PhotoService {
    UserRepository userRepository;
    PhotoRepository photoRepository;
    PhotoServiceHelper photoServiceHelper;
    @SneakyThrows
    @Override
    public UploadPhotoResponseBean uploadPhoto(MultipartFile file) {
        UploadPhotoResponseBean response=new UploadPhotoResponseBean();
        UserEnt user=userRepository.findByIdAndState(currentUserId,1).orElseThrow(()->new RuntimeException("User not found!"));

        byte[] bytes=file.getInputStream().readAllBytes();
        String[] fileNameParts=file.getOriginalFilename().split("\\.");
        String extension=fileNameParts[fileNameParts.length-1];

        if(!imageExtensions.contains(extension)){
            throw new RuntimeException("Unsupported file format!");
        }

        String photoPath =mediaPath+user.getUsername()+"_"+1+"."+extension;

        if(user.getPhoto()!=null){
            File oldPhoto=new File(user.getPhoto().getPath());
            if(oldPhoto.exists()){
                oldPhoto.delete();
            }
        }

        PhotoEnt photo=user.getPhoto();
        if(photo==null){
            photo=new PhotoEnt();
            photo.setUser(user);
        }
        photo.setPath(photoPath);
        photo.setFormat(extension);

        FileOutputStream outputStream=new FileOutputStream(photoPath);
        outputStream.write(bytes);

        user.setPhoto(photo);
        userRepository.save(user);
        return createResponse(response,"Photo uploaded successfully!");
    }

    @Override
    public DeletePhotoResponseBean deletePhoto() {
        DeletePhotoResponseBean response=new DeletePhotoResponseBean();

        UserEnt user=userRepository.findByIdAndState(currentUserId,1).orElseThrow(()->new RuntimeException("User not found!"));
        PhotoEnt photo=user.getPhoto();

        if(photo==null){
            throw new RuntimeException("You have not any photo!");
        }

        File file=new File(photo.getPath());

        if(file.exists()){
            file.delete();
        }

        user.setPhoto(null);
        userRepository.save(user);

        return createResponse(response,"Profile photo deleted successfully!");
    }

    @SneakyThrows
    @Override
    public ResponseEntity<byte[]> showProfilePhoto() {
        UserEnt user=userRepository.findByIdAndState(currentUserId,1).orElseThrow(()->new RuntimeException("User not found!"));
        PhotoEnt photo=user.getPhoto();
        if(photo==null){
            throw new RuntimeException("You have not any profile photo");
        }
        String photoPath=photo.getPath();
        File file=new File(photoPath);
        FileInputStream fileOutputStream =new FileInputStream(file);
        byte[] byteFile=fileOutputStream.readAllBytes();
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(photoServiceHelper.getMediaType(photo.getFormat()));
        return new ResponseEntity<>(byteFile,headers, HttpStatus.OK);
    }

}
