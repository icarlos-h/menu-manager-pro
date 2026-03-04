package com.menumgr.menumanagerprobackend.web.dto;

public record PublicMenuUnitHeaderResponse(
        Long id,
        String name,
        String phone,
        String instagram,
        String googleMapsUrl,
        String ifoodUrl,
        String keetaUrl,
        String food99Url
) {}