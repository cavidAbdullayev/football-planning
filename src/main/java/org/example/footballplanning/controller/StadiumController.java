package org.example.footballplanning.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.stadium.create.CreateStadiumRequestBean;
import org.example.footballplanning.bean.stadium.create.CreateStadiumResponseBean;
import org.example.footballplanning.bean.stadium.delete.DeleteStadiumRequestBean;
import org.example.footballplanning.bean.stadium.delete.DeleteStadiumResponseBean;
import org.example.footballplanning.bean.stadium.get.GetByNameRequestBean;
import org.example.footballplanning.bean.stadium.get.GetStadiumResponseBean;
import org.example.footballplanning.bean.stadium.getFilteredStadium.GetFilteredStadiumRequestBean;
import org.example.footballplanning.bean.stadium.update.UpdateStadiumRequestBean;
import org.example.footballplanning.bean.stadium.update.UpdateStadiumResponseBean;
import org.example.footballplanning.service.StadiumService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stadium")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StadiumController {
    StadiumService stadiumService;

    @GetMapping("/get-all")
    public ResponseEntity<List<GetStadiumResponseBean>> getAll() {
        List<GetStadiumResponseBean> response = stadiumService.getAll();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateStadiumResponseBean> create(
            @RequestBody CreateStadiumRequestBean request) {
        CreateStadiumResponseBean response = stadiumService.create(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<UpdateStadiumResponseBean> update(
            @RequestBody UpdateStadiumRequestBean request) {
        UpdateStadiumResponseBean response = stadiumService.update(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<DeleteStadiumResponseBean> delete(
            @RequestBody DeleteStadiumRequestBean request) {
        DeleteStadiumResponseBean response = stadiumService.delete(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/filtered")
    public List<GetStadiumResponseBean> getFilteredStadiums(GetFilteredStadiumRequestBean request) {
        return stadiumService.getFilteredStadiums(request);
    }
}