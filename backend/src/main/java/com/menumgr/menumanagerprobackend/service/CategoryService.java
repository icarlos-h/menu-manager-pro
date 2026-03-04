package com.menumgr.menumanagerprobackend.service;

import com.menumgr.menumanagerprobackend.domain.Category;
import com.menumgr.menumanagerprobackend.domain.UnitCategory;
import com.menumgr.menumanagerprobackend.repository.CategoryRepository;
import com.menumgr.menumanagerprobackend.repository.UnitCategoryRepository;
import com.menumgr.menumanagerprobackend.repository.UnitRepository;
import com.menumgr.menumanagerprobackend.web.dto.CategoryCreateRequest;
import com.menumgr.menumanagerprobackend.web.dto.CategoryResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;
    private final UnitRepository unitRepository;
    private final UnitCategoryRepository unitCategoryRepository;

    public CategoryService(CategoryRepository repository,
            UnitRepository unitRepository,
            UnitCategoryRepository unitCategoryRepository) {
this.repository = repository;
this.unitRepository = unitRepository;
this.unitCategoryRepository = unitCategoryRepository;
}

    @Transactional
    public CategoryResponse create(CategoryCreateRequest request) {
        String name = request.name().trim();

        if (repository.findByNameIgnoreCase(name).isPresent()) {
            throw new IllegalArgumentException("Category already exists: " + name);
        }

        Category saved = repository.save(new Category(name, null));
        unitRepository.findAll().forEach(unit -> {
            unitCategoryRepository.save(new UnitCategory(unit, saved, false));
        });

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Category not found: " + id);
        }
        
        try {
            repository.deleteById(id);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível excluir uma categoria que está em uso. Remova os produtos vinculados a essa categoria antes de excluir."
            );
        }
        
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(
                c.getId(),
                c.getName(),
                c.isActive()
        );
    }
}