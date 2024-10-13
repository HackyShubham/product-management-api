package com.example.product.repository;

import com.example.product.entity.Product;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

    public Uni<Product> findById(Long id) {
        return find("id", id).firstResult();
    }

    public Uni<Void> persistProduct(Product product) {
        return persist(product).replaceWithVoid();
    }

    public Uni<Boolean> deleteById(Long id) {
        return delete("id", id).map(deletedCount -> deletedCount > 0);
    }

    public Uni<List<Product>> findAllProducts() {
        return listAll();
    }

    public Uni<List<Product>> findAllSortedByPrice() {
        return find("order by price asc").list();
    }
}

