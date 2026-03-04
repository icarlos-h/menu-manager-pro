package com.menumgr.menumanagerprobackend.web;

import com.menumgr.menumanagerprobackend.service.UnitMenuService;
import com.menumgr.menumanagerprobackend.web.dto.UnitProductActiveRequest;
import com.menumgr.menumanagerprobackend.web.dto.UnitProductPriceRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unit")
public class UnitMenuController {

    private final UnitMenuService unitMenuService;

    public UnitMenuController(UnitMenuService unitMenuService) {
        this.unitMenuService = unitMenuService;
    }

    // PATCH /api/unit/products/{productId}/active
    @PatchMapping("/products/{productId}/active")
    public ResponseEntity<Void> setActive(@PathVariable Long productId,
                                         @Valid @RequestBody UnitProductActiveRequest request) {
        unitMenuService.setProductActive(productId, request.active());
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/unit/products/{productId}/price
    @PatchMapping("/products/{productId}/price")
    public ResponseEntity<Void> setPrice(@PathVariable Long productId,
                                         @RequestBody UnitProductPriceRequest request) {
        unitMenuService.setProductPriceOverride(productId, request.priceOverride());
        return ResponseEntity.noContent().build();
    }
}