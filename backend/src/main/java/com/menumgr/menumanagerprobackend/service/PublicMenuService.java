package com.menumgr.menumanagerprobackend.service;

import com.menumgr.menumanagerprobackend.domain.Unit;
import com.menumgr.menumanagerprobackend.domain.UnitCategory;
import com.menumgr.menumanagerprobackend.domain.UnitProduct;
import com.menumgr.menumanagerprobackend.repository.UnitCategoryRepository;
import com.menumgr.menumanagerprobackend.repository.UnitProductRepository;
import com.menumgr.menumanagerprobackend.repository.UnitRepository;
import com.menumgr.menumanagerprobackend.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PublicMenuService {

    private final UnitRepository unitRepository;
    private final UnitCategoryRepository unitCategoryRepository;
    private final UnitProductRepository unitProductRepository;

    public PublicMenuService(UnitRepository unitRepository,
                             UnitCategoryRepository unitCategoryRepository,
                             UnitProductRepository unitProductRepository) {
        this.unitRepository = unitRepository;
        this.unitCategoryRepository = unitCategoryRepository;
        this.unitProductRepository = unitProductRepository;
    }

    @Transactional(readOnly = true)
    public PublicMenuResponse getMenu(Long unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found: " + unitId));

        List<UnitCategory> activeCategories = unitCategoryRepository.findByUnitIdAndActiveTrue(unitId);
        List<UnitProduct> activeUnitProducts = unitProductRepository.findByUnitIdAndActiveTrue(unitId);

        // product list grouped by category id
        Map<Long, List<PublicMenuProductResponse>> productsByCategory = new HashMap<>();

        for (UnitProduct up : activeUnitProducts) {
            var p = up.getProduct();
            Long categoryId = p.getCategory().getId();

            BigDecimal finalPrice = (up.getPriceOverride() != null) ? up.getPriceOverride() : p.getBasePrice();

            productsByCategory.computeIfAbsent(categoryId, k -> new ArrayList<>())
                    .add(new PublicMenuProductResponse(
                            p.getId(),
                            p.getName(),
                            p.getDescription(),
                            finalPrice,
                            p.getPhotoUrl(),
                            p.getWeightGrams()
                    ));
        }

        List<PublicMenuCategoryResponse> categories = new ArrayList<>();
        for (UnitCategory uc : activeCategories) {
            var c = uc.getCategory();
            List<PublicMenuProductResponse> prods = productsByCategory.getOrDefault(c.getId(), List.of());
            if (!prods.isEmpty()) {
                categories.add(new PublicMenuCategoryResponse(c.getId(), c.getName(), prods));
            }
        }

        PublicMenuUnitHeaderResponse header = new PublicMenuUnitHeaderResponse(
                unit.getId(),
                unit.getName(),
                unit.getPhone(),
                unit.getInstagram(),
                unit.getGoogleMapsUrl(),
                unit.getIfoodUrl(),
                unit.getKeetaUrl(),
                unit.getFood99Url()
        );

        return new PublicMenuResponse(header, categories);
    }

    // DTOs (records) aqui dentro para facilitar (você pode depois mover para web/dto)
    public record PublicMenuResponse(
            PublicMenuUnitHeaderResponse unit,
            List<PublicMenuCategoryResponse> categories
    ) {}

    public record PublicMenuUnitHeaderResponse(
            Long id,
            String name,
            String phone,
            String instagram,
            String googleMapsUrl,
            String ifoodUrl,
            String keetaUrl,
            String food99Url
            
    ) {}

    public record PublicMenuCategoryResponse(
            Long id,
            String name,
            List<PublicMenuProductResponse> products
    ) {}

    public record PublicMenuProductResponse(
            Long id,
            String name,
            String description,
            BigDecimal price,
            String photoUrl,
            Integer weightGrams
    ) {}
}