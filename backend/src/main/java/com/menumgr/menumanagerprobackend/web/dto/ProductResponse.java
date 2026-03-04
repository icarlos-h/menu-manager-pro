package com.menumgr.menumanagerprobackend.web.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String name,
        String description,
        BigDecimal basePrice,
        String photoUrl,
        Integer weightGrams
) {}