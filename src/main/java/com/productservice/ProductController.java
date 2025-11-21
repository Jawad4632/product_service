package com.productservice;

import com.productservice.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody CreateProductRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(req));
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> list() {
        return ResponseEntity.ok(productService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody UpdateProductRequest req) {
        return ResponseEntity.ok(productService.updateProduct(id, req));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(@PathVariable Long id, @RequestBody StockUpdateRequest r) {
        productService.setStock(id, r.stock());
        return ResponseEntity.noContent().build();
    }

    // INTERNAL → reserve stock atomically
    @PostMapping("/internal/{id}/reserve")
    public ResponseEntity<ReserveResponse> reserve(@PathVariable Long id, @RequestBody ReserveRequest req) {
        boolean ok = productService.reserveStock(id, req.quantity());
        if (ok) return ResponseEntity.ok(new ReserveResponse(true, "reserved"));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ReserveResponse(false, "insufficient_stock"));
    }

    // INTERNAL → release stock (compensation)
    @PostMapping("/internal/{id}/release")
    public ResponseEntity<ReserveResponse> release(@PathVariable Long id, @RequestBody ReserveRequest req) {
        productService.releaseStock(id, req.quantity());
        return ResponseEntity.ok(new ReserveResponse(true, "released"));
    }
}
