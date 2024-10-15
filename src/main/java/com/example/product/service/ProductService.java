package com.example.product.service;

import com.example.product.dto.ProductDto;
import com.example.product.entity.Product;
import com.example.product.mapper.ProductMapper;
import com.example.product.repository.ProductRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;

    @Inject
    ProductMapper productMapper;

    @WithSession
    public Uni<ProductDto> getProductById(Long productId) {
        return productRepository.findById(productId)
                .onItem().ifNotNull().transform(this::entityToDto)
                .onItem().ifNull().failWith(() -> new WebApplicationException("Product not found", 404));
    }


    @WithSession
    public Uni<List<ProductDto>> getAllProducts() {
        return productRepository.findAllProducts()
                .onItem().transform(products -> products.stream()
                        .map(this::entityToDto)
                        .collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<ProductDto>> getAllProductsSortedByPrice() {
        return productRepository.findAllSortedByPrice()
                .onItem().transform(products -> products.stream()
                        .map(this::entityToDto)
                        .collect(Collectors.toList()));
    }

    @WithTransaction
    public Uni<ProductDto> createProduct(ProductDto productDto) {
        return productRepository.findBySku(productDto.getSku())
                .onItem().invoke(existingProduct -> {
                    if (existingProduct != null) {
                        throw new WebApplicationException("Product with SKU " + productDto.getSku() + " already exists", Response.Status.BAD_REQUEST);
                    }
                })
                .onItem().transformToUni(existingProduct -> {
                    // Create a new product since it does not exist
                    Product newProduct = mapToEntity(productDto);
                    // Persist the new product and return the resulting DTO
                    return productRepository.persist(newProduct)
                            .onItem().transform(savedProduct -> entityToDto(savedProduct));
                });
    }

    @WithTransaction
    public Uni<ProductDto> updateProduct(Long id, ProductDto ProductDto) {
        return productRepository.findById(id)
                .onItem().ifNull().failWith(() -> new WebApplicationException("Product not found", 404))
                .onItem().transformToUni(existingProduct -> {
                    // With Lombok, you can directly set the attributes without explicit setters
                    existingProduct.setName(ProductDto.getName());
                    existingProduct.setDescription(ProductDto.getDescription());
                    existingProduct.setPrice(ProductDto.getPrice());
                    existingProduct.setQuantity(ProductDto.getQuantity());
                    return productRepository.persist(existingProduct)
                            .onItem().transform(savedProduct -> entityToDto(savedProduct));
                });
    }

    @WithTransaction
    public Uni<Boolean> deleteProduct(Long id) {
        return productRepository.deleteById(id);
    }

    @WithSession
    public Uni<Boolean> checkStockAvailability(Long id, int count) {
        return productRepository.findById(id)
                .onItem().ifNotNull().transform(product -> product.getQuantity() >= count)
                .onItem().ifNull().failWith(() -> new WebApplicationException("Product not found", 404));
    }

    public Product mapToEntity(ProductDto productDTO) {
        return Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .quantity(productDTO.getQuantity())
                .sku(productDTO.getSku())
                .build();
    }

    public ProductDto entityToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .sku(product.getSku())
                .build();
    }


}

