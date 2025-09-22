package top.nguyennd.restsqlbackend.abstraction.pagedlist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import top.nguyennd.restsqlbackend.abstraction.dto.AbstractDto;
import top.nguyennd.restsqlbackend.abstraction.entity.AbstractEntity;
import top.nguyennd.restsqlbackend.abstraction.service.IBaseService;

import static top.nguyennd.restsqlbackend.abstraction.pagedlist.FilterTransform.getFieldTypes;
import static top.nguyennd.restsqlbackend.abstraction.pagedlist.FilterTransform.transformToAllowedFilter;

public interface IPagedListService<T extends AbstractEntity, V extends AbstractDto> extends IBaseService<T, V> {
    default Page<V> getPagedList(FilterReqDto filter, Pageable pageable) {
        var entityRepository = getRepository();
        var fieldMap = getFieldTypes(getEntityClass());
        Specification<T> specification = new FilterSpecificationGenerator<T>().generateSpecification(transformToAllowedFilter(filter, fieldMap));
        return entityRepository.findAll(specification, pageable).map(getMapper());
    }

}
