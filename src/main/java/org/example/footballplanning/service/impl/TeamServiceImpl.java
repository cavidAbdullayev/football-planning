package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.team.create.CreateTeamRequestBean;
import org.example.footballplanning.bean.team.create.CreateTeamResponseBean;
import org.example.footballplanning.bean.team.delete.DeleteTeamRequestBean;
import org.example.footballplanning.bean.team.delete.DeleteTeamResponseBean;
import org.example.footballplanning.bean.team.update.UpdateTeamRequestBean;
import org.example.footballplanning.bean.team.update.UpdateTeamResponseBean;
import org.example.footballplanning.exception.customExceptions.ObjectAlreadyExistsException;
import org.example.footballplanning.exception.customExceptions.TeamException;
import org.example.footballplanning.helper.TeamServiceHelper;
import org.example.footballplanning.helper.UserServiceHelper;
import org.example.footballplanning.model.child.TeamEnt;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.AnnouncementRepository;
import org.example.footballplanning.repository.RequestRepository;
import org.example.footballplanning.repository.TeamRepository;
import org.example.footballplanning.service.TeamService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.footballplanning.util.GeneralUtil.*;
import static org.example.footballplanning.staticData.GeneralStaticData.currentUserId;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    TeamRepository teamRepository;
    UserServiceHelper userServiceHelper;
    TeamServiceHelper teamServiceHelper;
    AnnouncementRepository announcementRepository;
    RequestRepository requestRepository;

    @Override
    public CreateTeamResponseBean create(CreateTeamRequestBean request) {
        UserEnt user = userServiceHelper.getUserById(currentUserId);

        if (user.getTeam() != null) {
            throw new ObjectAlreadyExistsException("User already has a team!");
        }

        validateFields(request);

        String teamName = request.getTeamName();
        if (teamRepository.existsByTeamName(teamName)) {
            throw new ObjectAlreadyExistsException("Team name already exists!");
        }

        TeamEnt team = new TeamEnt();

        mapFields(team, request);

        team.setManagerUser(user);
        teamServiceHelper.save(team);

        CreateTeamResponseBean response = new CreateTeamResponseBean();
        response.setTeamName(teamName);

        return createResponse(response, "Team created successfully!");
    }

    @Override
    public UpdateTeamResponseBean update(UpdateTeamRequestBean request) {
        UserEnt user = userServiceHelper.getUserById(currentUserId);

        validateFields(request);

        String teamNewName = request.getTeamNewName();

        TeamEnt team = user.getTeam();
        if (team == null) {
            throw new TeamException("You don't have a team!");
        }

        if (teamRepository.existsByTeamName(teamNewName) && (!teamNewName.equals(team.getTeamName()))) {
            throw new ObjectAlreadyExistsException("Team name already exists!");
        }

        updateDifferentFields(team, request);
        teamServiceHelper.save(team);

        UpdateTeamResponseBean response = new UpdateTeamResponseBean();
        response.setTeamName(request.getTeamNewName());

        return createResponse(response, "Team updated successfully!");
    }

    @Override
    @Transactional
    public DeleteTeamResponseBean delete(DeleteTeamRequestBean request) {
        UserEnt user = userServiceHelper.getUserById(currentUserId);

        validateFields(request);

        TeamEnt team = user.getTeam();
        if (team == null) {
            throw new TeamException("You don't have a team!");
        }

        announcementRepository.updateAnnouncementsStateByUser(user.getId(), 0);
        requestRepository.updateRequestsStateBySenderOrReceiver(user.getId(), 0);

        String teamName = request.getTeamName();

        teamServiceHelper.deleteById(team.getId());

        DeleteTeamResponseBean response = new DeleteTeamResponseBean();
        response.setTeamName(teamName);
        return createResponse(response, "Team deleted successfully!");
    }
}