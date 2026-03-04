package com.menumgr.menumanagerprobackend.web;

import com.menumgr.menumanagerprobackend.domain.UnitCategory;
import com.menumgr.menumanagerprobackend.domain.UnitProduct;
import com.menumgr.menumanagerprobackend.repository.UnitCategoryRepository;
import com.menumgr.menumanagerprobackend.repository.UnitProductRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public-menu")
public class PublicMenuController {

    private final UnitCategoryRepository unitCategoryRepository;
    private final UnitProductRepository unitProductRepository;

    public PublicMenuController(UnitCategoryRepository unitCategoryRepository,
                                UnitProductRepository unitProductRepository) {
        this.unitCategoryRepository = unitCategoryRepository;
        this.unitProductRepository = unitProductRepository;
    }

    @GetMapping("/units/{unitId}/menu")
    public MenuResponse getMenu(@PathVariable Long unitId) {

        // categorias ativas da unidade
        List<UnitCategory> unitCategories = unitCategoryRepository.findByUnitIdAndActiveTrue(unitId);

        // produtos ativos da unidade
        List<UnitProduct> unitProducts = unitProductRepository.findByUnitIdAndActiveTrue(unitId);

        // agrupa produtos por categoryId
        Map<Long, List<UnitProduct>> productsByCategoryId = unitProducts.stream()
                .collect(Collectors.groupingBy(up -> up.getProduct().getCategory().getId()));

        List<MenuCategoryResponse> categories = unitCategories.stream()
                .sorted(Comparator.comparingInt(uc -> uc.getCategory().getSortOrder()))
                .map(uc -> {
                    var category = uc.getCategory();
                    List<UnitProduct> prods = productsByCategoryId.getOrDefault(category.getId(), List.of());

                    List<MenuProductResponse> products = prods.stream()
                            .sorted(Comparator.comparing(up -> up.getProduct().getName().toLowerCase()))
                            .map(up -> {
                                var p = up.getProduct();
                                BigDecimal finalPrice = up.getPriceOverride() != null
                                        ? up.getPriceOverride()
                                        : p.getBasePrice();

                                return new MenuProductResponse(
                                        p.getId(),
                                        p.getName(),
                                        p.getDescription(),
                                        finalPrice,
                                        p.getPhotoUrl(),
                                        p.getWeightGrams()
                                );
                            })
                            .toList();

                    return new MenuCategoryResponse(
                            category.getId(),
                            category.getName(),
                            category.getSortOrder(),
                            products
                    );
                })
                .toList();

        return new MenuResponse(unitId, categories);
    }

    // -------- DTOs (records) --------

    public record MenuResponse(
            Long unitId,
            List<MenuCategoryResponse> categories
    ) {}

    public record MenuCategoryResponse(
            Long categoryId,
            String name,
            int sortOrder,
            List<MenuProductResponse> products
    ) {}

    public record MenuProductResponse(
            Long productId,
            String name,
            String description,
            BigDecimal price,
            String photoUrl,
            Integer weightGrams
    ) {}
}