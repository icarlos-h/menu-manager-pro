package com.menumgr.menumanagerprobackend.web.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UnitProductActiveRequest(
        @NotNull Boolean active
) {}

