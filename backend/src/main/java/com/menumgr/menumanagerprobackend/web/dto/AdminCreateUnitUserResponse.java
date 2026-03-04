package com.menumgr.menumanagerprobackend.web.dto;

public record AdminCreateUnitUserResponse(
        Long userId,
        Long unitId,
        String email,
        String temporaryPassword
) {}