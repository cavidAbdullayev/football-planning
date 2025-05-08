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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeamController {
    TeamService teamService;

    @PostMapping("/create")
    public ResponseEntity<CreateTeamResponseBean> create(
            @RequestBody CreateTeamRequestBean request) {
        CreateTeamResponseBean response = teamService.create(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<UpdateTeamResponseBean> update(
            @RequestBody UpdateTeamRequestBean request) {
        UpdateTeamResponseBean response = teamService.update(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<DeleteTeamResponseBean> delete(
            @RequestBody DeleteTeamRequestBean request) {
        DeleteTeamResponseBean response = teamService.delete(request);
        return ResponseEntity.ok(response);
    }
}
