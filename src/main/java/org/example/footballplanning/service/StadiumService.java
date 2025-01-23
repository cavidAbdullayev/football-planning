package org.example.footballplanning.service;

import org.example.footballplanning.bean.stadium.create.CreateStadiumRequestBean;
import org.example.footballplanning.bean.stadium.create.CreateStadiumResponseBean;
import org.example.footballplanning.bean.stadium.delete.DeleteStadiumRequestBean;
import org.example.footballplanning.bean.stadium.delete.DeleteStadiumResponseBean;
import org.example.footballplanning.bean.stadium.get.GetByNameRequestBean;
import org.example.footballplanning.bean.stadium.get.GetStadiumResponseBean;
import org.example.footballplanning.bean.stadium.update.UpdateStadiumRequestBean;
import org.example.footballplanning.bean.stadium.update.UpdateStadiumResponseBean;

import java.util.List;

public interface StadiumService {
    CreateStadiumResponseBean create(CreateStadiumRequestBean request);
    UpdateStadiumResponseBean update(UpdateStadiumRequestBean request);
    DeleteStadiumResponseBean delete(DeleteStadiumRequestBean request);
    GetStadiumResponseBean getByName(GetByNameRequestBean request);
    List<GetStadiumResponseBean>getAll();
}
