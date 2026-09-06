package com.tatastrive.erp.JAM.Enterprises.config;

import com.tatastrive.erp.JAM.Enterprises.Entity.AppUser;
import com.tatastrive.erp.JAM.Enterprises.Repository.AppUserRepository;
import com.tatastrive.erp.JAM.Enterprises.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AdminInitializer {

    @Value("${spring.security.user.name}")
    private String adminEmail;

    @Value("${spring.security.user.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner createInitialUsers(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate) {

        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                AppUser admin = new AppUser();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                admin.setEnabled(true);
                admin.setTemporaryPassword(false);
                userRepository.save(admin);
                System.out.println("Default admin created: " + adminEmail);
            }
        };
    }
}
