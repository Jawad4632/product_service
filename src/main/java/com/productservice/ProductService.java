package com.productservice;

import com.productservice.dto.CreateProductRequest;
import com.productservice.dto.ProductDto;
import com.productservice.dto.UpdateProductRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repo;

    @Transactional
    public boolean reserveStock(Long productId, int quantity) {
        int updated = repo.decrementStockIfAvailable(productId, quantity);
        return updated > 0;
    }
    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductRequest req) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setUpdatedAt(LocalDateTime.now());

        return toDto(repo.save(p));
    }

    @Transactional
    public void releaseStock(Long productId, int quantity) {
        Product p = repo.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found"));

        p.setStock(p.getStock() + quantity);
        repo.save(p);
    }

    @Transactional
    public void setStock(Long id, int newStock) {
        Product p = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));

        p.setStock(newStock);
        repo.save(p);
    }

    public ProductDto getProduct(Long id) {
        Product p = repo.findByIdAndStatus(id, "ACTIVE").orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return toDto(p);
    }

    public List<ProductDto> getAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public ProductDto createProduct(CreateProductRequest req) {
        Product p = Product.builder().name(req.name()).description(req.description()).price(req.price()).stock(req.stock()).status("ACTIVE").build();

        return toDto(repo.save(p));
    }

    private ProductDto toDto(Product p) {
        return new ProductDto(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(), p.getStatus());
    }
}
