package com.menumgr.menumanagerprobackend.repository;

import com.menumgr.menumanagerprobackend.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCategoryIdAndNameIgnoreCase(Long categoryId, String name);

}