package com.kirana.products.repository;

import com.kirana.products.entity.Product;
import com.kirana.products.repository.projection.ProductProjection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, UUID> {
  // TODO Switch to JPQL and handle sorting dynamically
  @Query(
      nativeQuery = true,
      value =
          "SELECT p.id, p.name, p.description, c.name as categoryName, "
              + "p.price, p.image_url as imageUrl, p.is_available as isAvailable, "
              + "p.created_at as createdAt, p.updated_at as updatedAt "
              + "FROM products p "
              + "JOIN categories c ON c.id = p.category_id "
              + "WHERE "
              + "(:category IS NULL OR LOWER(c.name) LIKE LOWER('%' || :category || '%')) "
              + "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER('%' || :name || '%'))",
      countQuery =
          "SELECT COUNT(*) "
              + "FROM products p "
              + "JOIN categories c ON c.id = p.category_id "
              + "WHERE "
              + "(:category IS NULL OR LOWER(c.name) LIKE LOWER('%' || :category || '%')) "
              + "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER('%' || :name || '%'))")
  Page<ProductProjection> findByCategoryAndName(
      @Param("category") String category, @Param("name") String name, Pageable pageable);
}
