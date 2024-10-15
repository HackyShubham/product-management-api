package com.example.product.resource;

import com.example.product.dto.ProductDto;
import com.example.product.service.ProductService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;

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
    //@WithTransaction
    //@Transactional
    public Uni<Response> createProduct(ProductDto productDto) {
        return productService.createProduct(productDto)
                .onItem().transform(productResponse -> Response
                        .status(CREATED) // Set the response status to 201
                        .entity(productResponse) // Set the response body
                        .build());
    }

    // Update an existing product
    @PUT
    @Path("/{id}")
    public Uni<Response> updateProduct(@PathParam("id") Long id, ProductDto productDto) {
        return productService.updateProduct(id,productDto)
                .onItem().transform(updatedProduct -> Response.ok(updatedProduct).build())
                .onFailure().recoverWithItem(th -> Response.status(Response.Status.NOT_FOUND).build());
    }

    // Delete a product by ID
    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteProduct(@PathParam("id") Long id) {
        return productService.deleteProduct(id)
                .onItem().transform(v -> Response.noContent().build())
                .onFailure().recoverWithItem(th -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/{id}/check-stock")
    public Uni<Response> checkStockAvailability(@PathParam("id") Long productId, @QueryParam("count") int count) {
        return productService.checkStockAvailability(productId, count)
                .onItem().transform(isAvailable -> isAvailable ?
                        Response.ok("Stock is available").build() :
                        Response.status(Response.Status.NOT_FOUND).entity("Insufficient stock").build());
    }

    @GET
    @Path("/sorted-by-price")
    public Uni<List<ProductDto>> getAllProductsSortedByPrice() {
        return productService.getAllProductsSortedByPrice();
    }
}

