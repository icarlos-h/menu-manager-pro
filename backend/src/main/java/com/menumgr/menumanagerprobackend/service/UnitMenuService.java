package com.menumgr.menumanagerprobackend.service;

import com.menumgr.menumanagerprobackend.domain.UnitProduct;
import com.menumgr.menumanagerprobackend.domain.User;
import com.menumgr.menumanagerprobackend.repository.UnitProductRepository;
import com.menumgr.menumanagerprobackend.repository.UserRepository;
import com.menumgr.menumanagerprobackend.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UnitMenuService {

    private final UnitProductRepository unitProductRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public UnitMenuService(UnitProductRepository unitProductRepository,
                           UserRepository userRepository,
                           AuthService authService) {
        this.unitProductRepository = unitProductRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional
    public void setProductActive(Long productId, boolean active) {
        User user = loadCurrentUser();
        Long unitId = authService.requireUnitId(user);

        UnitProduct up = unitProductRepository.findByUnitIdAndProductId(unitId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto não vinculado à unidade. unitId=" + unitId + ", productId=" + productId));

        up.setActive(active);
    }

    @Transactional
    public void setProductPriceOverride(Long productId, BigDecimal priceOverride) {
        User user = loadCurrentUser();
        Long unitId = authService.requireUnitId(user);

        UnitProduct up = unitProductRepository.findByUnitIdAndProductId(unitId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto não vinculado à unidade. unitId=" + unitId + ", productId=" + productId));

        // null => volta a usar basePrice
        up.setPriceOverride(priceOverride);
    }

    private User loadCurrentUser() {
        String email = authService.currentEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));
    }
}