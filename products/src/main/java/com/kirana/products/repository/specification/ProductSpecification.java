package com.kirana.products.repository.specification;

import com.kirana.products.entity.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

  public static Specification<Product> categoryFilterWithFetch(String category) {
    return (root, query, cb) -> {
      String normalizedCategory = StringUtils.lowerCase(StringUtils.trimToNull(category), Locale.ROOT);
      boolean isCountQuery = query != null && query.getResultType() == Long.class;

      // Non-count query: fetch category once so mapper won't trigger lazy loading.
      if (query != null && !isCountQuery) {
        root.fetch("category", JoinType.LEFT);
        query.distinct(true);
      }

      if (normalizedCategory == null) {
        return cb.conjunction();
      }

      if (isCountQuery) {
        // Count query: avoid fetch join.
        Join<Product, ?> categoryJoin = root.join("category", JoinType.LEFT);
        return cb.like(cb.lower(categoryJoin.get("name")), "%" + normalizedCategory + "%");
      }

      return cb.like(
          cb.lower(root.get("category").get("name")), "%" + normalizedCategory + "%");
    };
  }

  public static Specification<Product> hasName(String name) {
    return (root, query, cb) -> {
      String normalizedName = StringUtils.lowerCase(StringUtils.trimToNull(name), Locale.ROOT);
      if (normalizedName == null) return cb.conjunction();
      return cb.like(cb.lower(root.get("name")), "%" + normalizedName + "%");
    };
  }
}
