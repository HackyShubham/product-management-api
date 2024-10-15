package com.example.product.resource;

import com.example.product.dto.ProductDto;
import com.example.product.service.ProductService;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
class ProductResourceTest {

    @InjectMocks
    ProductResource productResource; // The resource being tested

    @Mock
    ProductService productService; // Mock the ProductService

    // Sample data for testing
    private static final ProductDto PRODUCT_A = new ProductDto(4000L, "Product A", "Description A", 100.0, 10, "SKU-A");
    private static final ProductDto PRODUCT_B = new ProductDto(5000L, "Product B", "Description B", 150.0, 5, "SKU-B");

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        // Mock the service call
        when(productService.getAllProducts()).thenReturn(Uni.createFrom().item(List.of(PRODUCT_A, PRODUCT_B)));

        // Execute the GET request
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get("/products")
                .then()
                .statusCode(200) // HTTP status code for OK
                .body("$", hasSize(greaterThan(10))); // Expect more than 10 products
    }

    @Test
    void testGetProductById() {
        // Mock the service call
        when(productService.getProductById(4000L)).thenReturn(Uni.createFrom().item(PRODUCT_A));

        // Execute the GET request
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get("/products/4000")
                .then()
                .statusCode(200) // HTTP status code for OK
                .body("name", is("Product A")); // Check name of product
    }

    @Test
    public void testCreateProduct() {
        ProductDto newProduct = new ProductDto();
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setPrice(99.99);
        newProduct.setQuantity(10);
        newProduct.setSku("NEW-SKU");

        given()
                .contentType(ContentType.JSON)
                .body(newProduct)
                .when()
                .post("/products")
                .then()
                .statusCode(201) // HTTP 201 Created
                .body("name", is("New Product"))
                .body("sku", is("NEW-SKU"));
    }

    @Test
    public void testUpdateProduct() {
        // First create a product to update
        ProductDto productToUpdate = new ProductDto();
        productToUpdate.setName("Product A");
        productToUpdate.setDescription("Description A");
        productToUpdate.setPrice(150.0);
        productToUpdate.setQuantity(5);
        productToUpdate.setSku("SKU-AAA");

        // Create the product first
        Integer productId = given()
                .contentType(ContentType.JSON)
                .body(productToUpdate)
                .when()
                .post("/products")
                .then()
                .statusCode(201) // HTTP 201 Created
                .extract().body().jsonPath().get("id");

        // Update the product
        ProductDto updatedProduct = new ProductDto();
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(199.99);
        updatedProduct.setQuantity(20);
        updatedProduct.setSku("SKU-BBB");

        given()
                .contentType(ContentType.JSON)
                .body(updatedProduct)

                .when()
                .put("/products/{id}", productId)
                .then()
                .statusCode(200) // HTTP 200 OK
                .body("name", is("Updated Product"))
                .body("price", is(199.99f));
    }

    @Test
    public void testCheckStockAvailability() {
        // Create a product for stock checking
        ProductDto stockProduct = new ProductDto();
        stockProduct.setName("Stock Product");
        stockProduct.setDescription("Stock Description");
        stockProduct.setPrice(120.0);
        stockProduct.setQuantity(15);
        stockProduct.setSku("STOCK-SKU");

        Integer productId = given()
                .contentType(ContentType.JSON)
                .body(stockProduct)
                .when()
                .post("/products")
                .then()
                .statusCode(201) // HTTP 201 Created
                .extract().body().jsonPath().get("id");

        // Check stock availability for a count less than quantity
        given()
                .queryParam("count", 10)
                .when()
                .get("/products/{id}/check-stock", productId)
                .then()
                .statusCode(200) // HTTP 200 OK
                .body(is("Stock is available"));

        // Check stock availability for a count greater than quantity
        given()
                .queryParam("count", 20)
                .when()
                .get("/products/{id}/check-stock", productId)
                .then()
                .statusCode(404) // HTTP 404 Not Found
                .body(is("Insufficient stock"));
    }

    @Test
    public void testDeleteProduct() {
        // Create a product to delete
        ProductDto productToDelete = new ProductDto();
        productToDelete.setName("Product to Delete");
        productToDelete.setDescription("Description to Delete");
        productToDelete.setPrice(80.0);
        productToDelete.setQuantity(2);
        productToDelete.setSku("DELETE-SKU");

        Integer productId = given()
                .contentType(ContentType.JSON)
                .body(productToDelete)
                .when()
                .post("/products")
                .then()
                .statusCode(201) // HTTP 201 Created
                .extract().body().jsonPath().get("id");

        // Delete the product
        given()
                .when()
                .delete("/products/{id}", productId)
                .then()
                .statusCode(204); // HTTP 204 No Content

        // Verify the product was deleted
        given()
                .when()
                .get("/products/{id}", productId)
                .then()
                .statusCode(404); // HTTP 404 Not Found
    }
}


