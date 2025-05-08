package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.exception.customExceptions.ObjectNotFoundException;
import org.example.footballplanning.model.child.StadiumEnt;
import org.example.footballplanning.repository.StadiumRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class StadiumServiceHelper {
    StadiumRepository stadiumRepository;


    @Cacheable(value = "stadiumCache", key = "#name")
    public StadiumEnt getByName(String name) {
        return stadiumRepository.findByName(name)
                .orElseThrow(() -> new ObjectNotFoundException("Stadium not found!"));
    }

    @CachePut(value = "stadiumCache", key = "'allStadiums'")
    public List<StadiumEnt> getAll() {
        return stadiumRepository.findAll();
    }

    @CachePut(value = "stadiumCache", key = "#p0.id")
    public StadiumEnt save(StadiumEnt stadium) {
        return stadiumRepository.save(stadium);
    }

    @CacheEvict(value = "stadiumCache", key = "#name")
    public void deleteByName(String name) {
        stadiumRepository.deleteByName(name);
    }
}