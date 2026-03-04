package com.menumgr.menumanagerprobackend.web;

import com.menumgr.menumanagerprobackend.repository.UnitRepository;
import com.menumgr.menumanagerprobackend.service.PublicMenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final UnitRepository unitRepository;
    private final PublicMenuService publicMenuService;

    public PublicController(UnitRepository unitRepository, PublicMenuService publicMenuService) {
        this.unitRepository = unitRepository;
        this.publicMenuService = publicMenuService;
    }

    @GetMapping("/units")
    public List<UnitResponse> listUnits() {
        return unitRepository.findAll()
                .stream()
                .map(u -> new UnitResponse(
                        u.getId(),
                        u.getName(),
                        u.getPhone(),
                        u.getInstagram()
                ))
                .toList();
    }

    @GetMapping("/units/{unitId}/menu")
    public PublicMenuService.PublicMenuResponse getMenu(@PathVariable Long unitId) {
        return publicMenuService.getMenu(unitId);
    }

    record UnitResponse(
            Long id,
            String name,
            String phone,
            String instagram
    ) {}
}