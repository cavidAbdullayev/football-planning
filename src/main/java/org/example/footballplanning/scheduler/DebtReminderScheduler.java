package org.example.footballplanning.scheduler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.helper.EmailServiceHelper;
import org.example.footballplanning.model.child.UserEnt;
import org.example.footballplanning.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class DebtReminderScheduler {
    UserRepository userRepository;
    EmailServiceHelper emailServiceHelper;

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDebtReminders() {
        List<UserEnt> usersWithDebt = userRepository.findAllByDebtGreaterThan(0d);
        for (UserEnt user : usersWithDebt) {
            String email = user.getEmail();
            double debt = user.getDebt();
            emailServiceHelper.sendEmailForDebt(email, debt);
        }
    }
}