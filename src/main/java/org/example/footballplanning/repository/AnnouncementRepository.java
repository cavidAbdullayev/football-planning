package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.AnnouncementEnt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<AnnouncementEnt,String> {

}
