package com.menumgr.menumanagerprobackend.web;

import com.menumgr.menumanagerprobackend.domain.Unit;
import com.menumgr.menumanagerprobackend.domain.User;
import com.menumgr.menumanagerprobackend.repository.UnitRepository;
import com.menumgr.menumanagerprobackend.repository.UserRepository;
import com.menumgr.menumanagerprobackend.service.AdminUserService;
import com.menumgr.menumanagerprobackend.web.dto.AdminCreateUnitUserRequest;
import com.menumgr.menumanagerprobackend.web.dto.AdminCreateUnitUserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUsersController {

    private final AdminUserService adminUserService;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;

    public AdminUsersController(
            AdminUserService adminUserService,
            UserRepository userRepository,
            UnitRepository unitRepository
    ) {
        this.adminUserService = adminUserService;
        this.userRepository = userRepository;
        this.unitRepository = unitRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminCreateUnitUserResponse createUnitUserAtRoot(@Valid @RequestBody AdminCreateUnitUserRequest request) {
        return adminUserService.createUnitUser(request);
    }

    // ✅ mantém endpoint legado
    @PostMapping("/unit")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminCreateUnitUserResponse createUnitUser(@Valid @RequestBody AdminCreateUnitUserRequest request) {
        return adminUserService.createUnitUser(request);
    }

    // ✅ novo: lista usuários existentes
    @GetMapping
    @Transactional(readOnly = true)
    public List<UserRowResponse> listUsers() {
        List<User> users = userRepository.findAll();

        Set<Long> unitIds = users.stream()
                .map(User::getUnitId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> unitNames = unitIds.isEmpty()
                ? Map.of()
                : unitRepository.findAllById(unitIds).stream()
                    .collect(Collectors.toMap(Unit::getId, Unit::getName));

        return users.stream()
                .sorted(Comparator.comparing(User::getId))
                .map(u -> new UserRowResponse(
                        u.getId(),
                        u.getEmail(),
                        u.getRole().name(),
                        u.getUnitId(),
                        u.getUnitId() == null ? null : unitNames.get(u.getUnitId()),
                        u.isActive()
                ))
                .toList();
    }

    // ✅ opcional: lista unidades para o select no front
    @GetMapping("/units")
    @Transactional(readOnly = true)
    public List<UnitOptionResponse> listUnitsOptions() {
        return unitRepository.findAll().stream()
                .sorted(Comparator.comparing(Unit::getId))
                .map(u -> new UnitOptionResponse(u.getId(), u.getName()))
                .toList();
    }
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
    }

    public record UserRowResponse(
            Long id,
            String email,
            String role,
            Long unitId,
            String unitName,
            boolean active
    ) {}

    public record UnitOptionResponse(
            Long id,
            String name
    ) {}
}