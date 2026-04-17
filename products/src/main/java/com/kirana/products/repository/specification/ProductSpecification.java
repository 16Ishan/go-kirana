package com.kirana.products.repository.specification;

import com.kirana.products.entity.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
  public static Specification<Product> hasCategory(String category) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(category)) return cb.conjunction();
      Join<Object, Object> categoryJoin = root.join("category", JoinType.LEFT);
      return cb.like(cb.lower(categoryJoin.get("name")), "%" + category.toLowerCase() + "%");
    };
  }

  public static Specification<Product> fetchCategory() {
    return (root, query, cb) -> {
      // skipping for count query
      if (query != null && query.getResultType() != Long.class) {
        root.fetch("category", JoinType.LEFT);
        query.distinct(true);
      }
      return cb.conjunction();
    };
  }

  public static Specification<Product> hasName(String name) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(name)) return cb.conjunction();
      return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    };
  }
}
