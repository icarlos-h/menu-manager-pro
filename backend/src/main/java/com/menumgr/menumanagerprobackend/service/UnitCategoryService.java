package com.menumgr.menumanagerprobackend.service;

import com.menumgr.menumanagerprobackend.domain.UnitCategory;
import com.menumgr.menumanagerprobackend.domain.User;
import com.menumgr.menumanagerprobackend.repository.UnitCategoryRepository;
import com.menumgr.menumanagerprobackend.repository.UserRepository;
import com.menumgr.menumanagerprobackend.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UnitCategoryService {

    private final UnitCategoryRepository unitCategoryRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public UnitCategoryService(UnitCategoryRepository unitCategoryRepository,
                               UserRepository userRepository,
                               AuthService authService) {
        this.unitCategoryRepository = unitCategoryRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional
    public void setCategoryActive(Long categoryId, boolean active) {
        User user = loadCurrentUser();
        Long unitId = authService.requireUnitId(user);

        UnitCategory uc = unitCategoryRepository.findByUnitIdAndCategoryId(unitId, categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not linked to unit. unitId=" + unitId + ", categoryId=" + categoryId));

        uc.setActive(active);
    }

    private User loadCurrentUser() {
        String email = authService.currentEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}