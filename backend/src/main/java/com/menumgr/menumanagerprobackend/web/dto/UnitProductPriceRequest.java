package com.menumgr.menumanagerprobackend.web.dto;

import java.math.BigDecimal;

public record UnitProductPriceRequest(
        BigDecimal priceOverride
) {}