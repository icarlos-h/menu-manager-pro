package com.menumgr.menumanagerprobackend.web;

import com.menumgr.menumanagerprobackend.service.UnitCategoryService;
import com.menumgr.menumanagerprobackend.web.dto.UnitCategoryActiveRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unit/categories")
public class UnitCategoryController {

    private final UnitCategoryService service;

    public UnitCategoryController(UnitCategoryService service) {
        this.service = service;
    }

    @PatchMapping("/{categoryId}/active")
    public ResponseEntity<Void> setActive(@PathVariable Long categoryId,
                                          @Valid @RequestBody UnitCategoryActiveRequest request) {
        service.setCategoryActive(categoryId, request.active());
        return ResponseEntity.noContent().build();
    }
}