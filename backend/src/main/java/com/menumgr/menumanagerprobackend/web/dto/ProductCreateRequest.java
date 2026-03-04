package com.menumgr.menumanagerprobackend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotNull Long categoryId,
        @NotBlank String name,
        String description,
        @NotNull BigDecimal basePrice,
        String photoUrl,
        Integer weightGrams
) {}