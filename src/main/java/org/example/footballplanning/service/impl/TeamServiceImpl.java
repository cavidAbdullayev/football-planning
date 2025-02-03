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
import org.example.footballplanning.model.child.TeamEnt;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.TeamRepository;
import org.example.footballplanning.repository.UserRepository;
import org.example.footballplanning.service.TeamService;
import org.springframework.stereotype.Service;

import static org.example.footballplanning.helper.GeneralHelper.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    TeamRepository teamRepository;
    UserRepository userRepository;

    @Override
    public CreateTeamResponseBean create(CreateTeamRequestBean request) {
        CreateTeamResponseBean response = new CreateTeamResponseBean();
        String username = request.getUsername();
        UserEnt user = userRepository.findByUsernameAndState(username, 1).orElseThrow(() -> new RuntimeException("User not found!"));
        if (user.getTeam() != null) {
            throw new RuntimeException("User already has team!");
        }
        String teamName = request.getTeamName();
        if (teamRepository.existsByTeamName(teamName)) {
            throw new RuntimeException("Team name already exists!");
        }
        validateFields(request);
        TeamEnt team = new TeamEnt();
        mapFields(team, request);
        team.setManagerUser(user);
        teamRepository.save(team);
        response.setTeamName(teamName);
        return createResponse(response, "Team created successfully!");
    }

    @Override
    public UpdateTeamResponseBean update(UpdateTeamRequestBean request) {
        UpdateTeamResponseBean response = new UpdateTeamResponseBean();
        validateFields(request);
        String teamName = request.getTeamOldName();
        String teamNewName = request.getTeamName();
        TeamEnt team = teamRepository.findByTeamName(teamName).orElseThrow(() -> new RuntimeException("Team not found!"));
        if (teamRepository.existsByTeamName(teamNewName) && (!teamNewName.equalsIgnoreCase(teamName))) {
            throw new RuntimeException("Team name already exists!");
        }
        updateDifferentFields(team, request);
        teamRepository.save(team);
        response.setTeamName(teamNewName);
        return createResponse(response, "Team updated successfully!");
    }

    @Override
    public DeleteTeamResponseBean delete(DeleteTeamRequestBean request) {
        DeleteTeamResponseBean response = new DeleteTeamResponseBean();
        validateFields(request);
        String teamName = request.getTeamName();
        TeamEnt team = teamRepository.findByTeamName(teamName).orElseThrow(() -> new RuntimeException("Team not found!"));
        teamRepository.deleteById(team.getId());
        response.setTeamName(teamName);
        return createResponse(response, "Team deleted successfully!");
    }
}