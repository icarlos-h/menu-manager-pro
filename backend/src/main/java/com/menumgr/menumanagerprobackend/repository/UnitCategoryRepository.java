package com.menumgr.menumanagerprobackend.repository;

import com.menumgr.menumanagerprobackend.domain.UnitCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnitCategoryRepository extends JpaRepository<UnitCategory, Long> {

    List<UnitCategory> findByUnitIdAndActiveTrue(Long unitId);

    Optional<UnitCategory> findByUnitIdAndCategoryId(Long unitId, Long categoryId);

    void deleteByUnitId(Long unitId);
}