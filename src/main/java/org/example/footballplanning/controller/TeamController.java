package org.example.footballplanning.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.team.create.CreateTeamRequestBean;
import org.example.footballplanning.bean.team.create.CreateTeamResponseBean;
import org.example.footballplanning.bean.team.delete.DeleteTeamRequestBean;
import org.example.footballplanning.bean.team.delete.DeleteTeamResponseBean;
import org.example.footballplanning.bean.team.update.UpdateTeamRequestBean;
import org.example.footballplanning.bean.team.update.UpdateTeamResponseBean;
import org.example.footballplanning.service.TeamService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class TeamController {
    TeamService teamService;

    @PostMapping("/create")
    public CreateTeamResponseBean create(@RequestBody CreateTeamRequestBean request) {
        return teamService.create(request);
    }

    @PutMapping("/update")
    public UpdateTeamResponseBean update(@RequestBody UpdateTeamRequestBean request) {
        return teamService.update(request);
    }

    @DeleteMapping("/delete")
    public DeleteTeamResponseBean delete(@RequestBody DeleteTeamRequestBean request) {
        return teamService.delete(request);
    }
}