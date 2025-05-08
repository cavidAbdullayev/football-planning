package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.request.get.GetRequestResponseBean;
import org.example.footballplanning.exception.customExceptions.ObjectNotFoundException;
import org.example.footballplanning.model.child.RequestEnt;
import org.example.footballplanning.repository.RequestRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.footballplanning.util.GeneralUtil.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RequestServiceHelper {
    UserServiceHelper userServiceHelper;
    RequestRepository requestRepository;

    public GetRequestResponseBean getRequestResponse(RequestEnt request) {
        return GetRequestResponseBean.builder()
                .to(userServiceHelper.getUserResponse(request.getTo()))
                .from(userServiceHelper.getUserResponse(request.getFrom()))
                .announcementId(request.getAnnouncement().getId())
                .timeStamp(dateTimeToStr(request.getCreatedDate()))
                .message(request.getMessage())
                .playerCount(request.getPlayerCount())
                .build();
    }

    public List<GetRequestResponseBean> getAllRequestsResponse(List<RequestEnt> requests) {
        return requests.stream()
                .map(this::getRequestResponse)
                .toList();
    }

    @CachePut(value = "requestCache", key = "#result.id")
    public RequestEnt save(RequestEnt request) {
        return requestRepository.save(request);
    }

    @Cacheable(value = "requestCache", key = "#requestId")
    public RequestEnt getById(Specification<RequestEnt> specification) {
        return requestRepository.findOne(specification)
                .orElseThrow(() -> new ObjectNotFoundException("You have not received a request with the given ID!"));
    }

    @CachePut(value = "requestCache")
    @Transactional
    public void saveAll(List<RequestEnt> requests) {
        requestRepository.saveAll(requests);
    }
}