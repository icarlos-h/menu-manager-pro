package com.menumgr.menumanagerprobackend.repository;

import com.menumgr.menumanagerprobackend.domain.UnitProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnitProductRepository extends JpaRepository<UnitProduct, Long> {

    List<UnitProduct> findByUnitIdAndActiveTrue(Long unitId);

    Optional<UnitProduct> findByUnitIdAndProductId(Long unitId, Long productId);
}