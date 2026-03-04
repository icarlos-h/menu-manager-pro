package com.menumgr.menumanagerprobackend.web.dto;

public record CategoryResponse(
        Long id,
        String name,
        boolean active
) {}