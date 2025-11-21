package com.productservice.dto;

import java.math.BigDecimal;

public record CreateProductRequest(String name, String description, BigDecimal price, int stock) {}

