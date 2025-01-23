package org.example.footballplanning.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.stadium.create.CreateStadiumRequestBean;
import org.example.footballplanning.bean.stadium.create.CreateStadiumResponseBean;
import org.example.footballplanning.bean.stadium.delete.DeleteStadiumRequestBean;
import org.example.footballplanning.bean.stadium.delete.DeleteStadiumResponseBean;
import org.example.footballplanning.bean.stadium.get.GetByNameRequestBean;
import org.example.footballplanning.bean.stadium.get.GetStadiumResponseBean;
import org.example.footballplanning.bean.stadium.update.UpdateStadiumRequestBean;
import org.example.footballplanning.bean.stadium.update.UpdateStadiumResponseBean;
import org.example.footballplanning.service.StadiumService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stadium")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class StadiumController {
    StadiumService stadiumService;
    @GetMapping("/get-by-name")
    public GetStadiumResponseBean getByName(@RequestBody GetByNameRequestBean request){
        return stadiumService.getByName(request);
    }
    @GetMapping("/get-all")
    public List<GetStadiumResponseBean>getAll(){
        return stadiumService.getAll();
    }
    @PostMapping("/create")
    public CreateStadiumResponseBean create(@RequestBody CreateStadiumRequestBean request){
        return stadiumService.create(request);
    }
    @PutMapping("/update")
    public UpdateStadiumResponseBean update(@RequestBody UpdateStadiumRequestBean request){
        return stadiumService.update(request);
    }
    @DeleteMapping("/delete")
    public DeleteStadiumResponseBean delete(@RequestBody DeleteStadiumRequestBean request){
        return stadiumService.delete(request);
    }

}
