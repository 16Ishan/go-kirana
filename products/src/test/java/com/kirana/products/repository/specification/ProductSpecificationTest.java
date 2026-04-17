package com.kirana.products.repository.specification;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kirana.products.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ProductSpecificationTest {

  @Mock private Root<Product> root;
  @Mock private CriteriaQuery<Product> productQuery;
  @Mock private CriteriaQuery<Long> countQuery;
  @Mock private CriteriaBuilder criteriaBuilder;
  @Mock private Predicate predicate;
  @Mock private Join<Product, Object> categoryJoin;
  @Mock private Path<String> categoryNamePath;
  @Mock private Expression<String> categoryLowerExpression;
  @Mock private Fetch<Product, Object> categoryFetch;
  @Mock private Path<String> namePath;
  @Mock private Expression<String> nameLowerExpression;

  // --- categoryFilterWithFetch ---

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "  "})
  void categoryFilterWithFetch_shouldFetchAndReturnConjunction_whenCategoryBlankOnNonCountQuery(
      String category) {
    when(productQuery.getResultType()).thenReturn(Product.class);
    when(root.<Product, Object>fetch(eq("category"), eq(JoinType.LEFT))).thenReturn(categoryFetch);
    when(criteriaBuilder.conjunction()).thenReturn(predicate);

    Specification<Product> specification = ProductSpecification.categoryFilterWithFetch(category);
    Predicate result = specification.toPredicate(root, productQuery, criteriaBuilder);

    assertSame(predicate, result);
    verify(root).fetch(eq("category"), eq(JoinType.LEFT));
    verify(productQuery).distinct(true);
    verify(root, never()).join(any(String.class), eq(JoinType.LEFT));
  }

  @Test
  void
      categoryFilterWithFetch_shouldFetchAndFilterByCategory_whenCategoryProvidedOnNonCountQuery() {
    when(productQuery.getResultType()).thenReturn(Product.class);
    when(root.<Product, Object>fetch(eq("category"), eq(JoinType.LEFT))).thenReturn(categoryFetch);
    when(root.<Product, Object>join(eq("category"), eq(JoinType.LEFT))).thenReturn(categoryJoin);
    when(categoryJoin.<String>get("name")).thenReturn(categoryNamePath);
    when(criteriaBuilder.lower(categoryNamePath)).thenReturn(categoryLowerExpression);
    when(criteriaBuilder.like(categoryLowerExpression, "%dairy%")).thenReturn(predicate);

    Specification<Product> specification = ProductSpecification.categoryFilterWithFetch("DAIRY");
    Predicate result = specification.toPredicate(root, productQuery, criteriaBuilder);

    assertSame(predicate, result);
    verify(root).fetch(eq("category"), eq(JoinType.LEFT));
    verify(productQuery).distinct(true);
    verify(root).join(eq("category"), eq(JoinType.LEFT));
    verify(criteriaBuilder).like(categoryLowerExpression, "%dairy%");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "  "})
  void categoryFilterWithFetch_shouldReturnConjunctionWithoutFetch_whenCategoryBlankOnCountQuery(
      String category) {
    when(countQuery.getResultType()).thenReturn(Long.class);
    when(criteriaBuilder.conjunction()).thenReturn(predicate);

    Specification<Product> specification = ProductSpecification.categoryFilterWithFetch(category);
    Predicate result = specification.toPredicate(root, countQuery, criteriaBuilder);

    assertSame(predicate, result);
    verify(root, never()).fetch(any(String.class), eq(JoinType.LEFT));
    verify(root, never()).join(any(String.class), eq(JoinType.LEFT));
  }

  @Test
  void
      categoryFilterWithFetch_shouldFilterByCategoryWithoutFetch_whenCategoryProvidedOnCountQuery() {
    when(countQuery.getResultType()).thenReturn(Long.class);
    when(root.<Product, Object>join(eq("category"), eq(JoinType.LEFT))).thenReturn(categoryJoin);
    when(categoryJoin.<String>get("name")).thenReturn(categoryNamePath);
    when(criteriaBuilder.lower(categoryNamePath)).thenReturn(categoryLowerExpression);
    when(criteriaBuilder.like(categoryLowerExpression, "%dairy%")).thenReturn(predicate);

    Specification<Product> specification = ProductSpecification.categoryFilterWithFetch("DAIRY");
    Predicate result = specification.toPredicate(root, countQuery, criteriaBuilder);

    assertSame(predicate, result);
    verify(root, never()).fetch(any(String.class), eq(JoinType.LEFT));
    verify(root).join(eq("category"), eq(JoinType.LEFT));
    verify(criteriaBuilder).like(categoryLowerExpression, "%dairy%");
  }

  // --- hasName ---

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "  "})
  void hasName_shouldReturnConjunction_whenNameBlank(String name) {
    when(criteriaBuilder.conjunction()).thenReturn(predicate);

    Specification<Product> specification = ProductSpecification.hasName(name);
    Predicate result = specification.toPredicate(root, productQuery, criteriaBuilder);

    assertSame(predicate, result);
    verify(criteriaBuilder).conjunction();
    verify(root, never()).get("name");
  }

  @Test
  void hasName_shouldBuildLikePredicate_whenNameProvided() {
    when(root.<String>get("name")).thenReturn(namePath);
    when(criteriaBuilder.lower(namePath)).thenReturn(nameLowerExpression);
    when(criteriaBuilder.like(nameLowerExpression, "%milk%")).thenReturn(predicate);

    Specification<Product> specification = ProductSpecification.hasName("MILK");
    Predicate result = specification.toPredicate(root, productQuery, criteriaBuilder);

    assertSame(predicate, result);
    verify(criteriaBuilder).like(nameLowerExpression, "%milk%");
  }
}
