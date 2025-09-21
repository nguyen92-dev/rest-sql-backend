package top.nguyennd.restsqlbackend.abstraction.service;

import top.nguyennd.restsqlbackend.abstraction.dto.AbstractDto;
import top.nguyennd.restsqlbackend.abstraction.entity.AbstractEntity;
import top.nguyennd.restsqlbackend.abstraction.repository.IBaseRepository;

import java.util.function.Function;

public interface IBaseService<T extends AbstractEntity, V extends AbstractDto> {
    IBaseRepository<T> getRepository();

    Function<T, V> getMapper();

    Class<T> getEntityClass();
}
