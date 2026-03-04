package com.menumgr.menumanagerprobackend.service;

import com.menumgr.menumanagerprobackend.repository.UnitCategoryRepository;
import com.menumgr.menumanagerprobackend.repository.UnitProductRepository;
import com.menumgr.menumanagerprobackend.repository.UnitRepository;
import com.menumgr.menumanagerprobackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUnitService {

    private final UnitRepository unitRepository;
    private final UnitCategoryRepository unitCategoryRepository;
    private final UnitProductRepository unitProductRepository;
    private final UserRepository userRepository;

    public AdminUnitService(UnitRepository unitRepository,
                            UnitCategoryRepository unitCategoryRepository,
                            UnitProductRepository unitProductRepository,
                            UserRepository userRepository) {
        this.unitRepository = unitRepository;
        this.unitCategoryRepository = unitCategoryRepository;
        this.unitProductRepository = unitProductRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void deleteUnit(Long unitId) {
        if (!unitRepository.existsById(unitId)) {
            throw new IllegalArgumentException("Unit not found: " + unitId);
        }

        userRepository.deleteByUnitId(unitId);
        unitProductRepository.deleteByUnitId(unitId);
        unitCategoryRepository.deleteByUnitId(unitId);
        unitRepository.deleteById(unitId);
    }
}