package top.nguyennd.restsqlbackend.abstraction.service;

import jakarta.persistence.Column;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import top.nguyennd.restsqlbackend.abstraction.common.ErrorStatus;
import top.nguyennd.restsqlbackend.abstraction.dto.AbstractDto;
import top.nguyennd.restsqlbackend.abstraction.entity.AbstractEntity;
import top.nguyennd.restsqlbackend.abstraction.exception.BusinessException;
import top.nguyennd.restsqlbackend.abstraction.pagedlist.FilterNodeDto;
import top.nguyennd.restsqlbackend.abstraction.pagedlist.FilterNodeType;
import top.nguyennd.restsqlbackend.abstraction.pagedlist.FilterOperator;
import top.nguyennd.restsqlbackend.abstraction.pagedlist.FilterReqDto;
import top.nguyennd.restsqlbackend.abstraction.pagedlist.FilterSpecificationGenerator;
import top.nguyennd.restsqlbackend.abstraction.pagedlist.FilterTransform;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static top.nguyennd.restsqlbackend.abstraction.pagedlist.FilterTransform.transformToAllowedFilter;
import static top.nguyennd.restsqlbackend.abstraction.utils.Validator.checkCondition;

public interface ICommonService<T extends AbstractEntity, V extends AbstractDto> extends IBaseService<T,V>{

    @Transactional(readOnly = true)
    default List<V> findAll() {
        return getRepository().findAll().stream().map(getMapper()).toList();
    }

    @Transactional(readOnly = true)
    default Optional<V> findById(Long id) {
        return getRepository().findById(id).map(getMapper());
    }

    @Transactional
    default V save(T entity) {
        return getMapper().apply(getRepository().save(entity));
    }

    @Transactional
    default void deleteById(Long id) {
        getRepository().findById(id).ifPresent(getRepository()::delete);
    }

    @Transactional
    default void saveAll(List<T> entities) {
        getRepository().saveAll(entities);
    }

    default void validateUniqueField(String fieldName, Object fieldValue, Long id) {
        List<T> savedEntities = findByFieldNames(fieldName, fieldValue);
        if (!savedEntities.isEmpty() && !savedEntities.get(0).getId().equals(id)) {
            throw BusinessException.conflict("%s %s %s đã được lưu".formatted(getEntityClass().getSimpleName(),fieldName, fieldValue));
        }
    }

    default List<T> findByFieldNames(String fieldName, Object fieldValue) {
        Map<String, Class<?>> fieldTypes = FilterTransform.getFieldTypes(getEntityClass());
        checkCondition(fieldTypes.containsKey(fieldName),
                "%s không có trường %s!".formatted(getEntityClass().getSimpleName(), fieldName));
        FilterNodeDto filterNode = FilterNodeDto.builder()
                .filterNodeType(FilterNodeType.LEAF)
                .filterOperator(FilterOperator.EQUAL)
                .filterValues(List.of(fieldValue))
                .build();
        FilterReqDto filter = new FilterReqDto(Map.of(fieldName, filterNode));
        Specification<T> specification = new FilterSpecificationGenerator<T>().generateSpecification(transformToAllowedFilter(filter, fieldTypes), true);
        return getRepository().findAll(specification);
    }

    default void validateUniqueFields(AbstractDto reqDto, Long id) {
        Map<String, Object> uniqueFieldMap = null;
        try {
            uniqueFieldMap = getUniqueFields(reqDto);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw BusinessException.internalServerError("Hệ thống đã có lỗi xảy ra vui lòng truy cập lại sau");
        }
        uniqueFieldMap.forEach((fieldName, fieldValue) -> validateUniqueField(fieldName, fieldValue, id));
    }

    private Map<String, Object> getUniqueFields(AbstractDto reqDto) throws NoSuchFieldException, IllegalAccessException {
        Map<String, Object> uniqueFieldMap = new HashMap<>();
        Field[] declaredFields = getEntityClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Column.class)
                    && field.getAnnotation(Column.class).unique()) {
                Field reqUniqueField = reqDto.getClass().getDeclaredField(field.getName());
                reqUniqueField.setAccessible(true);
                uniqueFieldMap.put(field.getName(), reqUniqueField.get(reqDto));
            }
        }
        return uniqueFieldMap;
    }
}
