package com.menumgr.menumanagerprobackend.web;

import com.menumgr.menumanagerprobackend.service.AdminProductService;
import com.menumgr.menumanagerprobackend.service.ProductService;
import com.menumgr.menumanagerprobackend.web.dto.ProductCreateRequest;
import com.menumgr.menumanagerprobackend.web.dto.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService service;
    private final AdminProductService adminProductService;

    public AdminProductController(ProductService service, AdminProductService adminProductService) {
        this.service = service;
        this.adminProductService = adminProductService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductCreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ProductResponse> list() {
        return service.list();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    
    @PostMapping("/{id}/image")
    public ProductResponse uploadImage(@PathVariable Long id,
                                       @RequestParam("image") MultipartFile image) {
        return adminProductService.uploadProductImage(id, image);
    }
}