package com.menumgr.menumanagerprobackend.web.dto;

import java.util.List;

public record PublicMenuCategoryResponse(
        Long id,
        String name,
        List<PublicMenuProductResponse> products
) {}