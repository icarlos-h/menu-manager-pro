package com.menumgr.menumanagerprobackend.bootstrap;

import com.menumgr.menumanagerprobackend.domain.*;
import com.menumgr.menumanagerprobackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class AdminSeed {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            UnitRepository unitRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            UnitCategoryRepository unitCategoryRepository,
            UnitProductRepository unitProductRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            seedAdminUser(userRepository, passwordEncoder);
            Unit unit = seedUnit(unitRepository);

            Category burgers = upsertCategory(categoryRepository, "Burgers", 1);
            Category drinks  = upsertCategory(categoryRepository, "Bebidas", 2);

            Product p1 = upsertProduct(productRepository, burgers, "Cheeseburger", "Pão, carne, queijo", new BigDecimal("24.90"), null, 250);
            Product p2 = upsertProduct(productRepository, burgers, "Bacon Burger", "Bacon crocante + cheddar", new BigDecimal("29.90"), null, 280);
            Product p3 = upsertProduct(productRepository, drinks, "Refrigerante Lata", "350ml", new BigDecimal("6.50"), null, 350);

            upsertUnitCategory(unitCategoryRepository, unit, burgers, true);
            upsertUnitCategory(unitCategoryRepository, unit, drinks, true);

            upsertUnitProduct(unitProductRepository, unit, p1, true, null); // usa basePrice
            upsertUnitProduct(unitProductRepository, unit, p2, true, new BigDecimal("31.90")); // override
            upsertUnitProduct(unitProductRepository, unit, p3, true, null);
        };
    }

    // -------------------- ADMIN --------------------

    private void seedAdminUser(UserRepository userRepository, PasswordEncoder encoder) {
        String email = "admin@menumgr.com";
        if (!userRepository.existsByEmail(email)) {
            User admin = new User(email, encoder.encode("admin123"), Role.ADMIN, null);
            userRepository.save(admin);
        }
    }

    // -------------------- UNIT --------------------

    private Unit seedUnit(UnitRepository unitRepository) {
        return unitRepository.findAll().stream().findFirst()
                .orElseGet(() -> unitRepository.save(new Unit("Unidade Centro", "+55 11 99999-9999", "@unidadecentro")));
    }
    // -------------------- CATEGORY --------------------

    private Category upsertCategory(CategoryRepository repo, String name, int sortOrder) {
        return repo.findByNameIgnoreCase(name)
                .orElseGet(() -> repo.save(new Category( name, sortOrder)));
    }

    // -------------------- PRODUCT --------------------

    private Product upsertProduct(ProductRepository repo, Category category, String name, String desc,
                                  BigDecimal basePrice, String photoUrl, Integer grams) {

        return repo.findByCategoryIdAndNameIgnoreCase(category.getId(), name)
                .orElseGet(() -> repo.save(new Product(category, name, desc, basePrice, photoUrl, grams)));
    }

    // -------------------- UNIT CATEGORY --------------------

    private void upsertUnitCategory(UnitCategoryRepository repo, Unit unit, Category category, boolean active) {
        UnitCategory uc = repo.findByUnitIdAndCategoryId(unit.getId(), category.getId())
                .orElseGet(() -> new UnitCategory(unit, category, active));

        uc.setActive(active);
        repo.save(uc);
    }

    // -------------------- UNIT PRODUCT --------------------

    private void upsertUnitProduct(UnitProductRepository repo, Unit unit, Product product, boolean active, BigDecimal priceOverride) {
        UnitProduct up = repo.findByUnitIdAndProductId(unit.getId(), product.getId())
                .orElseGet(() -> new UnitProduct(unit, product, active, priceOverride));

        up.setActive(active);
        up.setPriceOverride(priceOverride);
        repo.save(up);
    }
}