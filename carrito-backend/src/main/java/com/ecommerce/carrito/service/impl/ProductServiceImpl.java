package com.ecommerce.carrito.service.impl;

import com.ecommerce.carrito.dto.ProductResponseDto;
import com.ecommerce.carrito.exception.EmptyListException;
import com.ecommerce.carrito.exception.EntityNotFoundException;
import com.ecommerce.carrito.model.Product;
import com.ecommerce.carrito.repository.ProductRepository;
import com.ecommerce.carrito.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            throw new EmptyListException("No existen productos en la base de datos");
        }

        return products.stream()
                .map(product -> modelMapper.map(product, ProductResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        validateProductId(id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe el producto con ID: " + id));

        return modelMapper.map(product, ProductResponseDto.class);
    }

    @Override
    public Product findProductById(Long id) {
        validateProductId(id);

        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El producto con ID " + id + " no existe"));
    }

    private void validateProductId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID no válido");
        }
    }
}
