package com.tacticboard.persistence.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class QueryUtils {

    private QueryUtils() {
        throw new AssertionError("QueryUtils class should not be instantiated");
    }

    /**
     * Creates a dynamic query with filters
     * @param entityManager JPA EntityManager
     * @param entityClass Entity class
     * @param filters Map of field name and value to filter
     * @param pageable Pagination information
     * @param <T> Entity type
     * @return Page of entities matching the filters
     */
    public static <T> Page<T> findByFilters(
            EntityManager entityManager,
            Class<T> entityClass,
            Map<String, Object> filters,
            Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        List<Predicate> predicates = new ArrayList<>();
        
        // Add filters to predicates
        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                if (value != null) {
                    if (value instanceof String) {
                        predicates.add(cb.like(cb.lower(root.get(key)), "%" + ((String) value).toLowerCase() + "%"));
                    } else {
                        predicates.add(cb.equal(root.get(key), value));
                    }
                }
            }
        }
        
        // Apply predicates to query
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        
        // Count query for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(entityClass);
        countQuery.select(cb.count(countRoot));
        
        if (!predicates.isEmpty()) {
            countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        
        Long total = entityManager.createQuery(countQuery).getSingleResult();
        
        // Apply sorting
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                if (order.isAscending()) {
                    query.orderBy(cb.asc(root.get(order.getProperty())));
                } else {
                    query.orderBy(cb.desc(root.get(order.getProperty())));
                }
            });
        }
        
        // Create query with pagination
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        
        List<T> results = typedQuery.getResultList();
        
        return new PageImpl<>(results, pageable, total);
    }
    
    /**
     * Executes a native SQL query and maps results to the entity class
     * @param entityManager JPA EntityManager
     * @param sql Native SQL query
     * @param entityClass Entity class
     * @param <T> Entity type
     * @return List of entities
     */
    public static <T> List<T> executeNativeQuery(
            EntityManager entityManager,
            String sql,
            Class<T> entityClass) {
        
        return entityManager.createNativeQuery(sql, entityClass).getResultList();
    }
}