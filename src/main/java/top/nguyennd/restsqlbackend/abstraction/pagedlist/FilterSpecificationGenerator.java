package top.nguyennd.restsqlbackend.abstraction.pagedlist;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@AllArgsConstructor
@NoArgsConstructor
public class FilterSpecificationGenerator<T> {
    Map<String, Class<?>> fieldTypeMap;

    public Specification<T> generateSpecification(FilterReqDto filter) {
        if (nonNull(filter)) {
            return ((root, query, criteriaBuilder) -> {
               List<Predicate> predicates = new ArrayList<>();
               if (nonNull(filter.getFilters())) {
                   filter.getFilters().forEach((attributeName, filterNode) -> {
                       predicates.add(buildPredicate(root, criteriaBuilder, attributeName, filterNode));
                   });
               }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            });
        }
        return null;
    }

    private Predicate buildPredicate(Root<T> root, CriteriaBuilder criteriaBuilder, String attributeName, FilterNodeDto filterNode) {
        return switch (filterNode.getFilterNodeType()) {
            case OR -> buildOrPredicate(root, criteriaBuilder, attributeName, filterNode);
            case AND -> buildAndPredicate(root, criteriaBuilder, attributeName, filterNode);
            case LEAF -> buildLeafPredicate(root, criteriaBuilder, attributeName, filterNode);
            case NOT ->  criteriaBuilder.not(buildPredicate(root, criteriaBuilder, attributeName, filterNode));
        };
    }

    private Predicate buildAndPredicate(Root<T> root, CriteriaBuilder criteriaBuilder, String attributeName, FilterNodeDto filterNode) {
        List<Predicate> predicates = new ArrayList<>();
        filterNode.getSubFilterNodes().forEach(subFilterNode -> {
            predicates.add(buildPredicate(root, criteriaBuilder, attributeName, subFilterNode));
        });
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate buildOrPredicate(Root<T> root, CriteriaBuilder criteriaBuilder, String attributeName, FilterNodeDto filterNode) {
        List<Predicate> orPredicates = new ArrayList<>();
        filterNode.getSubFilterNodes().forEach(subFilterNode -> {
            orPredicates.add(buildPredicate(root, criteriaBuilder, attributeName, subFilterNode));
        });
        return criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
    }

    private Predicate buildLeafPredicate(Root<T> root, CriteriaBuilder criteriaBuilder, String attributeName, FilterNodeDto filterNode) {
        var operator = filterNode.getFilterOperator();
        var values = filterNode.getFilterValues();
        var path = getAttributePath(root, attributeName);
        return switch (operator) {
            case EQUAL -> criteriaBuilder.equal(path, values.getFirst());
            case UNEQUAL -> criteriaBuilder.notEqual(path, values.getFirst());
            case GREATER -> addGreaterPredicate(criteriaBuilder, path, values);
            case GREATER_EQUAL -> addGreaterEqualPredicate(criteriaBuilder, path, values);
            case LESS -> addLessPredicate(criteriaBuilder, path, values);
            case LESS_EQUAL -> addLessEqualPredicate(criteriaBuilder, path, values);
            case IN -> path.in(values);
            case NOT_IN -> criteriaBuilder.not(path.in(values));
            case CONTAINS -> criteriaBuilder.like(path.as(String.class), "%" + values.getFirst() + "%");
            case CONTAINS_NOT -> criteriaBuilder.notLike(path.as(String.class), "%" + values.getFirst() + "%");
            case IS_NULL -> criteriaBuilder.isNull(path);
        };
    }

    private Predicate addLessEqualPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, List<Object> values) {
        var javaType = path.getJavaType();
        return switch (javaType.getSimpleName()) {
            case "Integer" -> criteriaBuilder.lessThanOrEqualTo(path.as(Integer.class), (Integer) values.getFirst());
            case "Long" -> criteriaBuilder.lessThanOrEqualTo(path.as(Long.class), (Long) values.getFirst());
            case "Double" -> criteriaBuilder.lessThanOrEqualTo(path.as(Double.class), (Double) values.getFirst());
            case "LocalDate" -> criteriaBuilder.lessThanOrEqualTo(path.as(LocalDate.class), (LocalDate) values.getFirst());
            case "LocalDateTime" ->
                    criteriaBuilder.lessThanOrEqualTo(path.as(LocalDateTime.class), (LocalDateTime) values.getFirst());
            default -> criteriaBuilder.lessThanOrEqualTo(path.as(String.class), values.getFirst().toString());
        };
    }

    private Predicate addLessPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, List<Object> values) {
        var javaType = path.getJavaType();
        return switch (javaType.getSimpleName()) {
            case "Integer" -> criteriaBuilder.lessThan(path.as(Integer.class), (Integer) values.getFirst());
            case "Long" -> criteriaBuilder.lessThan(path.as(Long.class), (Long) values.getFirst());
            case "Double" -> criteriaBuilder.lessThan(path.as(Double.class), (Double) values.getFirst());
            case "LocalDate" -> criteriaBuilder.lessThan(path.as(LocalDate.class), (LocalDate) values.getFirst());
            case "LocalDateTime" ->
                    criteriaBuilder.lessThan(path.as(LocalDateTime.class), (LocalDateTime) values.getFirst());
            default -> criteriaBuilder.lessThan(path.as(String.class), values.getFirst().toString());
        };
    }

    private Predicate addGreaterEqualPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, List<Object> values) {
        Class<?> javaType = path.getJavaType();
        return switch (javaType.getSimpleName()) {
            case "Integer" -> criteriaBuilder.greaterThanOrEqualTo(path.as(Integer.class), (Integer) values.getFirst());
            case "Long" -> criteriaBuilder.greaterThanOrEqualTo(path.as(Long.class), (Long) values.getFirst());
            case "Double" -> criteriaBuilder.greaterThanOrEqualTo(path.as(Double.class), (Double) values.getFirst());
            case "LocalDate" -> criteriaBuilder.greaterThanOrEqualTo(path.as(LocalDate.class), (LocalDate) values.getFirst());
            case "LocalDateTime" ->
                    criteriaBuilder.greaterThanOrEqualTo(path.as(LocalDateTime.class), (LocalDateTime) values.getFirst());
            default -> criteriaBuilder.greaterThanOrEqualTo(path.as(String.class), values.getFirst().toString());
        };
    }

    private Predicate addGreaterPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, List<Object> values) {
        Class<?> javaType = path.getJavaType();
        return switch (javaType.getSimpleName()) {
            case "Integer" -> criteriaBuilder.greaterThan(path.as(Integer.class), (Integer) values.getFirst());
            case "Long" -> criteriaBuilder.greaterThan(path.as(Long.class), (Long) values.getFirst());
            case "Double" -> criteriaBuilder.greaterThan(path.as(Double.class), (Double) values.getFirst());
            case "LocalDate" -> criteriaBuilder.greaterThan(path.as(LocalDate.class), (LocalDate) values.getFirst());
            case "LocalDateTime" ->
                    criteriaBuilder.greaterThan(path.as(LocalDateTime.class), (LocalDateTime) values.getFirst());
            default -> criteriaBuilder.greaterThan(path.as(String.class), values.getFirst().toString());
        };
    }

    private Path<?> getAttributePath(Root<T> root, String attributeName) {
        var fields = attributeName.split("\\.");
        Path<?> path = root.get(fields[0]);
        if (fields.length > 1) {
            for (int i = 1; i < fields.length; i++) {
                path = path.get(fields[i]);
            }
        }
        return path;
    }
}
