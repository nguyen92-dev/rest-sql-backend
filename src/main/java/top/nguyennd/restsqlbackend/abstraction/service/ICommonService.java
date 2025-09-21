package top.nguyennd.restsqlbackend.abstraction.service;

import org.springframework.transaction.annotation.Transactional;
import top.nguyennd.restsqlbackend.abstraction.dto.AbstractDto;
import top.nguyennd.restsqlbackend.abstraction.entity.AbstractEntity;

import java.util.List;
import java.util.Optional;

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
        getRepository().deleteById(id);
    }

    @Transactional
    default void saveAll(List<T> entities) {
        getRepository().saveAll(entities);
    }
}
