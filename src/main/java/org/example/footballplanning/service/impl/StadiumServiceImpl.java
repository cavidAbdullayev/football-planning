package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.stadium.create.CreateStadiumRequestBean;
import org.example.footballplanning.bean.stadium.create.CreateStadiumResponseBean;
import org.example.footballplanning.bean.stadium.delete.DeleteStadiumRequestBean;
import org.example.footballplanning.bean.stadium.delete.DeleteStadiumResponseBean;
import org.example.footballplanning.bean.stadium.get.GetStadiumResponseBean;
import org.example.footballplanning.bean.stadium.getFilteredStadium.GetFilteredStadiumRequestBean;
import org.example.footballplanning.bean.stadium.update.UpdateStadiumRequestBean;
import org.example.footballplanning.bean.stadium.update.UpdateStadiumResponseBean;
import org.example.footballplanning.exception.customExceptions.ObjectAlreadyExistsException;
import org.example.footballplanning.exception.customExceptions.ValidationException;
import org.example.footballplanning.helper.StadiumServiceHelper;
import org.example.footballplanning.helper.UserServiceHelper;
import org.example.footballplanning.model.child.StadiumEnt;
import org.example.footballplanning.repository.StadiumRepository;
import org.example.footballplanning.service.StadiumService;
import org.example.footballplanning.specifications.StadiumSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.footballplanning.util.GeneralUtil.*;
import static org.example.footballplanning.staticData.GeneralStaticData.currentUserId;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class StadiumServiceImpl implements StadiumService {
    StadiumRepository stadiumRepository;
    StadiumServiceHelper stadiumServiceHelper;
    UserServiceHelper userServiceHelper;

    @SneakyThrows
    @Override
    public CreateStadiumResponseBean create(CreateStadiumRequestBean request) {
        validateFields(request);

        String name = request.getName();

        if (stadiumRepository.existsByName(name)) {
            throw new ObjectAlreadyExistsException("Stadium name already exists!");
        }

        StadiumEnt stadium = mapFields(new StadiumEnt(), request);
        stadiumServiceHelper.save(stadium);

        CreateStadiumResponseBean response = new CreateStadiumResponseBean();

        response.setMessage("Stadium created successfully!");

        return mapFields(response, stadium);
    }

    @Override
    @Transactional
    public UpdateStadiumResponseBean update(UpdateStadiumRequestBean request) {
        UpdateStadiumResponseBean response = new UpdateStadiumResponseBean();

        String name = request.getName();
        if (isNullOrEmpty(name)) {
            throw new ValidationException("Invalid request body!");
        }

        if (stadiumRepository.existsByName(name)) {
            throw new ObjectAlreadyExistsException("Stadium name already exists!");
        }

        StadiumEnt stadium = stadiumServiceHelper.getByName(name);

        updateDifferentFields(stadium, request);

        stadiumServiceHelper.save(stadium);

        response.setMessage("Stadium created successfully!");

        return mapFields(response, stadium);
    }

    @Override
    @Transactional
    public DeleteStadiumResponseBean delete(DeleteStadiumRequestBean request) {
        String name = request.getName();
        if (isNullOrEmpty(name)) {
            throw new ValidationException("Invalid request body!");
        }

        StadiumEnt stadium = stadiumServiceHelper.getByName(name);
        stadiumServiceHelper.deleteByName(name);

        DeleteStadiumResponseBean response = new DeleteStadiumResponseBean();

        response.setMessage("Stadium deleted successfully!");

        return mapFields(response, stadium);
    }

    @Override
    public List<GetStadiumResponseBean> getFilteredStadiums(GetFilteredStadiumRequestBean request) {

        request.normalize();

        int page=request.getPage();
        int size=request.getSize();
        String name=request.getName();
        String location=request.getLocation();
        Double maxHourlyRate= request.getMaxHourlyRate();
        Double minHourlyRate= request.getMinHourlyRate();

        Pageable pageable = PageRequest.of(page, size);

        Specification<StadiumEnt> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(StadiumSpecifications.hasName(name));
        }

        if (location != null && !location.isEmpty()) {
            spec = spec.and(StadiumSpecifications.hasLocation(location));
        }

        if (minHourlyRate != null && maxHourlyRate != null) {
            spec = spec.and(StadiumSpecifications.hasHourlyRateBetween(minHourlyRate, maxHourlyRate));
        }

        List<StadiumEnt> stadiums = stadiumRepository.findAll(spec, pageable)
                .toList();

        return stadiums.stream()
                .map(stadium -> {
            GetStadiumResponseBean response = new GetStadiumResponseBean();
            mapFields(response, stadium);
            return response;
        }).toList();

    }

    @Override
    public List<GetStadiumResponseBean> getAll() {
        userServiceHelper.getUserById(currentUserId);

        return stadiumServiceHelper.getAll()
                .stream()
                .map(s -> mapFields(new GetStadiumResponseBean(), s))
                .toList();
    }
}