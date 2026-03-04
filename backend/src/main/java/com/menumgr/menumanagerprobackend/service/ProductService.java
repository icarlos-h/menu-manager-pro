package com.menumgr.menumanagerprobackend.service;

import com.menumgr.menumanagerprobackend.domain.Category;
import com.menumgr.menumanagerprobackend.domain.Product;
import com.menumgr.menumanagerprobackend.repository.CategoryRepository;
import com.menumgr.menumanagerprobackend.repository.ProductRepository;
import com.menumgr.menumanagerprobackend.web.dto.ProductCreateRequest;
import com.menumgr.menumanagerprobackend.web.dto.ProductResponse;
import com.menumgr.menumanagerprobackend.domain.Unit;
import com.menumgr.menumanagerprobackend.domain.UnitProduct;
import com.menumgr.menumanagerprobackend.repository.UnitRepository;
import com.menumgr.menumanagerprobackend.repository.UnitProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final UnitRepository unitRepository;
	private final UnitProductRepository unitProductRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
			UnitRepository unitRepository, UnitProductRepository unitProductRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.unitRepository = unitRepository;
		this.unitProductRepository = unitProductRepository;
	}

	@Transactional
	public ProductResponse create(ProductCreateRequest request) {
		String name = request.name().trim();

		Category category = categoryRepository.findById(request.categoryId())
				.orElseThrow(() -> new IllegalArgumentException("Category not found: " + request.categoryId()));

		if (productRepository.findByCategoryIdAndNameIgnoreCase(category.getId(), name).isPresent()) {
			throw new IllegalArgumentException("Product already exists in this category: " + name);
		}

		Product saved = productRepository.save(new Product(category, name, normalizeNullable(request.description()),
				request.basePrice(), normalizeNullable(request.photoUrl()), request.weightGrams()));
		// Create UnitProduct for every unit (inactive by default)
		unitRepository.findAll().forEach(unit -> {
		    unitProductRepository.save(new UnitProduct(unit, saved, false, null));
		});

		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> list() {
		// N+1 aqui não é crítico agora, mas depois a gente pode melhorar com
		// @EntityGraph / join fetch
		return productRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional
	public void delete(Long id) {
		if (!productRepository.existsById(id)) {
			throw new IllegalArgumentException("Product not found: " + id);
		}
		productRepository.deleteById(id);
	}

	private ProductResponse toResponse(Product p) {
		return new ProductResponse(p.getId(), p.getCategory().getId(), p.getCategory().getName(), p.getName(),
				p.getDescription(), p.getBasePrice(), p.getPhotoUrl(), p.getWeightGrams());
	}

	private String normalizeNullable(String s) {
		if (s == null)
			return null;
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}

}