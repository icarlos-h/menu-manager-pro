package com.menumgr.menumanagerprobackend.web.dto;

import jakarta.validation.constraints.NotNull;

public record UnitCategoryActiveRequest(
        @NotNull Boolean active
) {}