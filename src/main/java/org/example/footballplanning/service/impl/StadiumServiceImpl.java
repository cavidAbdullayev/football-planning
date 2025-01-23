package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.stadium.create.CreateStadiumRequestBean;
import org.example.footballplanning.bean.stadium.create.CreateStadiumResponseBean;
import org.example.footballplanning.bean.stadium.delete.DeleteStadiumRequestBean;
import org.example.footballplanning.bean.stadium.delete.DeleteStadiumResponseBean;
import org.example.footballplanning.bean.stadium.get.GetByNameRequestBean;
import org.example.footballplanning.bean.stadium.get.GetStadiumResponseBean;
import org.example.footballplanning.bean.stadium.update.UpdateStadiumRequestBean;
import org.example.footballplanning.bean.stadium.update.UpdateStadiumResponseBean;
import org.example.footballplanning.model.child.StadiumEnt;
import org.example.footballplanning.repository.StadiumRepository;
import org.example.footballplanning.service.StadiumService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.footballplanning.helper.GeneralHelper.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class StadiumServiceImpl implements StadiumService {
    StadiumRepository stadiumRepository;
    @SneakyThrows
    @Override
    public CreateStadiumResponseBean create(CreateStadiumRequestBean request) {
        CreateStadiumResponseBean response=new CreateStadiumResponseBean();
        validateFields(request);
        String name=request.getName();
        if(stadiumRepository.existsByName(name)){
            throw new RuntimeException("Stadium name already exists!");
        }
        StadiumEnt stadium=mapFields(new StadiumEnt(),request);
        stadiumRepository.save(stadium);
        response.setMessage("Stadium created successfully!");
        return mapFields(response,stadium);
    }

    @Override
    @Transactional
    public UpdateStadiumResponseBean update(UpdateStadiumRequestBean request) {
        UpdateStadiumResponseBean response=new UpdateStadiumResponseBean();
        String name=request.getName();
        if(isNullOrEmpty(name)){
            throw new RuntimeException("Invalid request body!");
        }
        if(stadiumRepository.existsByName(name)){
            throw new RuntimeException("Stadium name already exists!");
        }
        StadiumEnt stadium=stadiumRepository.findByName(name).orElseThrow(()->new RuntimeException("Stadium not found!"));
        updateDifferentFields(stadium,request);
        stadiumRepository.save(stadium);
        response.setMessage("Stadium created successfully!");
        return mapFields(response,stadium);
    }

    @Override
    @Transactional
    public DeleteStadiumResponseBean delete(DeleteStadiumRequestBean request) {
        DeleteStadiumResponseBean response=new DeleteStadiumResponseBean();
        String name=request.getName();
        if(isNullOrEmpty(name)){
            throw new RuntimeException("Invalid request body!");
        }
        StadiumEnt stadium=stadiumRepository.findByName(name).orElseThrow(()->new RuntimeException("Stadium not found!"));
        stadiumRepository.deleteByName(name);
        response.setMessage("Stadium deleted successfully!");
        return mapFields(response,stadium);
    }

    @Override
    public GetStadiumResponseBean getByName(GetByNameRequestBean request) {
        GetStadiumResponseBean response=new GetStadiumResponseBean();
        String name=request.getName();
        if(isNullOrEmpty(name)){
            throw new RuntimeException("Invalid request body!");
        }
        StadiumEnt stadium=stadiumRepository.findByName(name).orElseThrow(()->new RuntimeException("Stadium not found!"));
        mapFields(response,stadium);
        return response;
    }

    @Override
    public List<GetStadiumResponseBean> getAll() {
        List<StadiumEnt>stadiums=stadiumRepository.findAll();
        return stadiums.stream().map(s->mapFields(new GetStadiumResponseBean(),s)).toList();
    }


}
