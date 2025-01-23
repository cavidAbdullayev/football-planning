package org.example.footballplanning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FootballPlanningApplication {

    public static void main(String[] args) {
        SpringApplication.run(FootballPlanningApplication.class, args);
    }

}
