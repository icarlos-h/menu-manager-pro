package com.menumgr.menumanagerprobackend.repository;

import com.menumgr.menumanagerprobackend.domain.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    Optional<Unit> findFirstByOrderByIdAsc();
}