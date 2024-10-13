package com.example.product.mapper;

import com.example.product.dto.ProductDto;
import com.example.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface ProductMapper {
    /*@Mapping(source = "product.id", target = "id")
    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "product.description", target = "description")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "product.quantity", target = "quantity")*/
    ProductDto toDto(Product product);
    Product toEntity(ProductDto productDto);
    List<ProductDto> toDtoList(List<Product> products);
}
