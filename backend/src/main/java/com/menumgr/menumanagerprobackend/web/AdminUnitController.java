package com.menumgr.menumanagerprobackend.web;

import com.menumgr.menumanagerprobackend.domain.Unit;
import com.menumgr.menumanagerprobackend.repository.UnitRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/units")
public class AdminUnitController {

    private final UnitRepository unitRepository;

    public AdminUnitController(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    // ✅ LIST
    @GetMapping
    @Transactional(readOnly = true)
    public List<UnitResponse> list() {
        return unitRepository.findAll().stream()
                .map(UnitResponse::from)
                .toList();
    }

    // ✅ CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public UnitResponse create(@Valid @RequestBody CreateUnitRequest req) {
        Unit u = new Unit(req.name().trim(), req.phone().trim(), req.instagram().trim());

        // opcionais (se vier null ou vazio, fica null no banco e o front pode ocultar)
        u.setGoogleMapsUrl(normalizeOptional(req.googleMapsUrl()));
        u.setIfoodUrl(normalizeOptional(req.ifoodUrl()));
        u.setKeetaUrl(normalizeOptional(req.keetaUrl()));
        u.setFood99Url(normalizeOptional(req.food99Url()));

        Unit saved = unitRepository.save(u);
        return UnitResponse.from(saved);
    }

    private String normalizeOptional(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // DTO de criação (record)
    public record CreateUnitRequest(
            @NotBlank String name,
            @NotBlank String phone,
            @NotBlank String instagram,
            String googleMapsUrl,
            String ifoodUrl,
            String keetaUrl,
            String food99Url
    ) {}

    // DTO de resposta (record)
    public record UnitResponse(
            Long id,
            String name,
            String phone,
            String instagram,
            String googleMapsUrl,
            String ifoodUrl,
            String keetaUrl,
            String food99Url
    ) {
        static UnitResponse from(Unit u) {
            return new UnitResponse(
                    u.getId(),
                    u.getName(),
                    u.getPhone(),
                    u.getInstagram(),
                    u.getGoogleMapsUrl(),
                    u.getIfoodUrl(),
                    u.getKeetaUrl(),
                    u.getFood99Url()
            );
        }
    }
}