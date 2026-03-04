package com.menumgr.menumanagerprobackend.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminCreateUnitUserRequest(
        @NotNull Long unitId,
        @NotBlank @Email String email
) {}