package com.ecommerce.carrito.service;

import com.ecommerce.carrito.dto.ProductResponseDto;
import com.ecommerce.carrito.model.Product;

import java.util.List;

public interface IProductService {

    List<ProductResponseDto> getAllProducts();

    ProductResponseDto getProductById(Long id);

    Product findProductById(Long id);
}
