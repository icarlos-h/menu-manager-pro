package com.menumgr.menumanagerprobackend.service;

import com.menumgr.menumanagerprobackend.domain.Role;
import com.menumgr.menumanagerprobackend.domain.Unit;
import com.menumgr.menumanagerprobackend.domain.User;
import com.menumgr.menumanagerprobackend.repository.UnitRepository;
import com.menumgr.menumanagerprobackend.repository.UserRepository;
import com.menumgr.menumanagerprobackend.web.dto.AdminCreateUnitUserRequest;
import com.menumgr.menumanagerprobackend.web.dto.AdminCreateUnitUserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(UserRepository userRepository,
                            UnitRepository unitRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.unitRepository = unitRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AdminCreateUnitUserResponse createUnitUser(AdminCreateUnitUserRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        Unit unit = unitRepository.findById(request.unitId())
                .orElseThrow(() -> new IllegalArgumentException("Unit not found: " + request.unitId()));

        String tempPassword = generateTempPassword(10);
        String encoded = passwordEncoder.encode(tempPassword);

        User user = new User(email, encoded, Role.UNIT, unit.getId());
        User saved = userRepository.save(user);

        return new AdminCreateUnitUserResponse(
                saved.getId(),
                unit.getId(),
                saved.getEmail(),
                tempPassword
        );
    }

    
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        userRepository.deleteById(userId);
    }

    private String generateTempPassword(int length) {
        final String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }
}