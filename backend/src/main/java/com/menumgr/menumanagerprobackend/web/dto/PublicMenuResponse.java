package com.menumgr.menumanagerprobackend.web.dto;

import java.util.List;

public record PublicMenuResponse(
        PublicMenuUnitHeaderResponse unit,
        List<PublicMenuCategoryResponse> categories
) {}