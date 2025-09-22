package top.nguyennd.restsqlbackend.abstraction.pagedlist;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import top.nguyennd.restsqlbackend.abstraction.entity.AbstractEntity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static top.nguyennd.restsqlbackend.abstraction.utils.Validator.checkCondition;

@UtilityClass
public class FilterTransform {

    public static Map<String, Class<?>> getFieldTypes(Class<?> clazz) {
        Map<String, Class<?>> declaredFields = new HashMap<>();
        while (!(clazz == null || clazz.equals(Object.class))) {
            declaredFields.putAll(Arrays.stream(clazz.getDeclaredFields())
                    .collect(Collectors.toMap(Field::getName, Field::getType)));
            clazz = clazz.getSuperclass();
        }
        return declaredFields;
    }

    public static FilterReqDto transformToAllowedFilter(FilterReqDto filter, Map<String, Class<?>> fieldTypes) {
        if (nonNull(filter) && nonNull(filter.getFilters())) {
            filter.getFilters().forEach((fieldName, filterNode) -> {
                Class<?> existingFieldType = checkFieldInEntityExists(fieldTypes, fieldName);
                convertTypeSafelyForFilterValue(filterNode, existingFieldType);
            });
        }
        return filter;
    }

    private static void convertTypeSafelyForFilterValue(FilterNodeDto filterNode, Class<?> existingFieldType) {
        validateFilterNode(filterNode);
        if (nonNull(filterNode.getFilterValues())) {
            filterNode.setFilterValues(parseFilterValue(filterNode.getFilterValues(), existingFieldType));
        }
        if (nonNull(filterNode.getSubFilterNodes())) {
            filterNode.getSubFilterNodes().forEach(node -> 
                    convertTypeSafelyForFilterValue(node, existingFieldType));
        }
    }

    private static List<Object> parseFilterValue(List<Object> filterValues, Class<?> existingFieldType) {
        return filterValues.stream().map(value ->
                switch (existingFieldType.getSimpleName()) {
                    case "Integer" -> Integer.parseInt(value.toString());
                    case "Long" -> Long.parseLong(value.toString());
                    case "Double" -> Double.parseDouble(value.toString());
                    case "LocalDate" -> java.time.LocalDate.parse(value.toString());
                    case "LocalDateTime" -> java.time.LocalDateTime.parse(value.toString());
                    default -> value;
                }).toList();
    }

    private static void validateFilterNode(FilterNodeDto filterNode) {
        checkCondition(nonNull(filterNode), "Filter node is missing");
        checkCondition(nonNull(filterNode.getFilterNodeType()), "Filter node type is missing");
        checkCondition(filterNode.getFilterNodeType().equals(FilterNodeType.LEAF)
                && !filterNode.getFilterOperator().equals(FilterOperator.IS_NULL)
                && nonNull(filterNode.getFilterValues())
                && !filterNode.getFilterValues().isEmpty(),
                "Missing filter value in filter node %s".formatted(filterNode));
    }

    private static Class<?> checkFieldInEntityExists(Map<String, Class<?>> fieldTypes, String fieldName) {
        checkCondition(StringUtils.isNotBlank(fieldName), "Field name is invalid %s".formatted(fieldName));
        var fieldPath = fieldName.split("\\.");
        Class<?> existingFieldType = null;
        if (fieldPath.length > 0) {
             checkCondition(fieldTypes.containsKey(fieldPath[0]), "Field path is invalid %s".formatted(fieldName));
        }
        if (fieldPath.length == 1) {
            existingFieldType = fieldTypes.get(fieldPath[0]);
        }
        if (fieldPath.length > 1) {
            checkCondition(AbstractEntity.class.isAssignableFrom(fieldTypes.get(fieldPath[0])),
                    "Referenced field %s is not an entity".formatted(fieldPath[0]));
            existingFieldType = checkFieldInEntityExists(getFieldTypes(fieldTypes.get(fieldPath[0])),
                    fieldPath[1]);
        }
        return existingFieldType;
    }
}
