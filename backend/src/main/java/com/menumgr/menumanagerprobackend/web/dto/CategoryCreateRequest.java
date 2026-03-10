package com.menumgr.menumanagerprobackend.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank String name,
        Integer sortOrder
) {}