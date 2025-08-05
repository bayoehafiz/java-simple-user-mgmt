package com.example.userapi.health;

import com.example.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Custom health indicator to check the status of the user repository
 * and its underlying data storage.
 */
@Component
public class UserRepositoryHealthIndicator implements HealthIndicator {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Health health() {
        try {
            // Check if we can access the user repository
            int userCount = userRepository.findAll().size();
            
            // Check if data file exists and is accessible (for file-based storage)
            File dataFile = new File("users.json");
            boolean fileAccessible = !dataFile.exists() || dataFile.canRead();
            
            if (fileAccessible) {
                return Health.up()
                    .withDetail("message", "User repository is healthy")
                    .withDetail("userCount", userCount)
                    .withDetail("dataFileAccessible", fileAccessible)
                    .withDetail("dataFilePath", dataFile.getAbsolutePath())
                    .build();
            } else {
                return Health.down()
                    .withDetail("message", "Data file is not accessible")
                    .withDetail("dataFilePath", dataFile.getAbsolutePath())
                    .build();
            }
            
        } catch (Exception e) {
            return Health.down()
                .withDetail("message", "User repository check failed")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
