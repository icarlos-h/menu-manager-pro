package com.menumgr.menumanagerprobackend.service;

import com.menumgr.menumanagerprobackend.domain.Category;
import com.menumgr.menumanagerprobackend.domain.Product;
import com.menumgr.menumanagerprobackend.repository.CategoryRepository;
import com.menumgr.menumanagerprobackend.repository.ProductRepository;
import com.menumgr.menumanagerprobackend.web.dto.ProductCreateRequest;
import com.menumgr.menumanagerprobackend.web.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public AdminProductService(ProductRepository productRepository,
                               CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // =========================
    // LIST
    // =========================
    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // CREATE (JSON)
    // =========================
    @Transactional
    public ProductResponse create(ProductCreateRequest req) {
        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada. categoryId=" + req.categoryId()));

        Product p = new Product(
                category,
                req.name().trim(),
                req.description(),
                req.basePrice(),
                req.photoUrl(),
                req.weightGrams()
        );

        try {
            p = productRepository.save(p);
        } catch (DataIntegrityViolationException e) {
            // Você tem unique (category_id, name) e/ou outros constraints
            throw new IllegalArgumentException("Já existe um produto com esse nome nessa categoria.");
        }

        return toResponse(p);
    }

    // =========================
    // UPLOAD IMAGE
    // POST /api/admin/products/{id}/image  (multipart)
    // =========================
    @Transactional
    public ProductResponse uploadProductImage(Long productId, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de imagem é obrigatório.");
        }

        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado. id=" + productId));

        // pasta: ./uploads/products
        Path baseDir = Paths.get(uploadDir, "products").toAbsolutePath().normalize();

        try {
            Files.createDirectories(baseDir);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao criar diretório de upload: " + baseDir, e);
        }

        String ext = resolveExtension(image);
        String slug = slugify(p.getName());
        String filename = slug + "." + ext;

        Path target = baseDir.resolve(filename).normalize();

        try (InputStream in = image.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao salvar imagem do produto.", e);
        }

        // URL pública que o frontend usa
        String publicUrl = "/uploads/products/" + filename;
        p.setPhotoUrl(publicUrl);

        p = productRepository.save(p);
        return toResponse(p);
    }

    // =========================
    // Helpers
    // =========================
    private ProductResponse toResponse(Product p) {
        // IMPORTANTE: isso aqui precisa bater com o seu ProductResponse atual.
        // Se o seu record for diferente, me manda ele que eu ajusto.
        return new ProductResponse(
                p.getId(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getName(),
                p.getDescription(),
                p.getBasePrice(),
                p.getPhotoUrl(),
                p.getWeightGrams()
        );
    }

    private String resolveExtension(MultipartFile file) {
        // tenta pelo filename
        String original = file.getOriginalFilename();
        if (original != null) {
            int idx = original.lastIndexOf('.');
            if (idx > -1 && idx < original.length() - 1) {
                String ext = original.substring(idx + 1).toLowerCase(Locale.ROOT);
                if (ext.matches("png|jpg|jpeg|webp")) {
                    return ext.equals("jpeg") ? "jpg" : ext;
                }
            }
        }

        // tenta pelo content-type
        String ct = file.getContentType();
        if (ct != null) {
            if (ct.equalsIgnoreCase(MediaType.IMAGE_PNG_VALUE)) return "png";
            if (ct.equalsIgnoreCase(MediaType.IMAGE_JPEG_VALUE)) return "jpg";
            if (ct.equalsIgnoreCase("image/webp")) return "webp";
        }

        // fallback
        return "jpg";
    }

    private String slugify(String input) {
        if (input == null) return "produto";
        String s = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        s = s.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return s.isBlank() ? "produto" : s;
    }
}