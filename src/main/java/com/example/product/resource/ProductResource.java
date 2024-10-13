package com.example.product.resource;

import com.example.product.dto.ProductDto;
import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    // Get all products
    @GET
    public Uni<List<ProductDto>> getAllProducts() {
        return productService.getAllProducts();
    }

    // Get a product by ID
    @GET
    @Path("/{id}")
    public Uni<ProductDto> getProductById(@PathParam("id") Long id) {
        return productService.getProductById(id);
    }

    // Create a new product
    @POST
    @Transactional
    public Uni<Void> createProduct(ProductDto ProductDto) {
        return productService.createProduct(ProductDto);
    }

    // Update an existing product
    @PUT
    @Path("/{id}")
    @Transactional
    public Uni<Response> updateProduct(@PathParam("id") Long id, ProductDto productDto) {
        return productService.updateProduct(id,productDto)
                .onItem().transform(updatedProduct -> Response.ok(updatedProduct).build())
                .onFailure().recoverWithItem(th -> Response.status(Response.Status.NOT_FOUND).build());
    }

    // Delete a product by ID
    @DELETE
    @Path("/{id}")
    @Transactional
    public Uni<Response> deleteProduct(@PathParam("id") Long id) {
        return productService.deleteProduct(id)
                .onItem().transform(v -> Response.noContent().build())
                .onFailure().recoverWithItem(th -> Response.status(Response.Status.NOT_FOUND).build());
    }

    // Get all products sorted by price
   /* @GET
    @Path("/sorted")
    public Uni<List<ProductDto>> getProductsSortedByPrice() {
        return productService.getProductsSortedByPrice();
    }*/
}

