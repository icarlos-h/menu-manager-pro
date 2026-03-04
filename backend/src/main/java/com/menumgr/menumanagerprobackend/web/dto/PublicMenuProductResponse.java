package com.menumgr.menumanagerprobackend.web.dto;

import java.math.BigDecimal;

public record PublicMenuProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String photoUrl,
        Integer weightGrams
) {}