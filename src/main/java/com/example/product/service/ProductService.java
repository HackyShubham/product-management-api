package com.example.product.service;

import com.example.product.dto.ProductDto;
import com.example.product.entity.Product;
import com.example.product.mapper.ProductMapper;
import com.example.product.repository.ProductRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import java.util.List;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;

    @Inject
    ProductMapper productMapper;

    @WithSession
    public Uni<ProductDto> getProductById(Long id) {
        return productRepository.findById(id)
                .onItem().invoke(product -> System.out.println("Fetched product: " + product))
                .onItem().ifNotNull().transform(this::entityToDto)
                .onItem().ifNull().failWith(() -> new WebApplicationException("Product not found", 404));
    }

    public Uni<List<ProductDto>> getAllProducts() {
        return productRepository.findAllProducts()
                .onItem().transform(productMapper::toDtoList);
    }

    public Uni<List<ProductDto>> getAllProductsSortedByPrice() {
        return productRepository.findAllSortedByPrice()
                .onItem().transform(productMapper::toDtoList);
    }

    public Uni<Void> createProduct(ProductDto ProductDto) {
        Product product = productMapper.toEntity(ProductDto);
        return productRepository.persistProduct(product);
    }

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
                            .replaceWith(productMapper.toDto(existingProduct));
                });
    }

    public Uni<Boolean> deleteProduct(Long id) {
        return productRepository.deleteById(id);
    }

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
                .build();
    }

    public ProductDto entityToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }


}

