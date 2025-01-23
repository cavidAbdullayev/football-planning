package org.example.footballplanning.service;

import org.example.footballplanning.bean.team.create.CreateTeamRequestBean;
import org.example.footballplanning.bean.team.create.CreateTeamResponseBean;
import org.example.footballplanning.bean.team.delete.DeleteTeamRequestBean;
import org.example.footballplanning.bean.team.delete.DeleteTeamResponseBean;
import org.example.footballplanning.bean.team.update.UpdateTeamRequestBean;
import org.example.footballplanning.bean.team.update.UpdateTeamResponseBean;

public interface TeamService {
    CreateTeamResponseBean create(CreateTeamRequestBean request);
    UpdateTeamResponseBean update(UpdateTeamRequestBean request);
    DeleteTeamResponseBean delete(DeleteTeamRequestBean request);
}
