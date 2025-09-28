package top.nguyennd.restsqlbackend.abstraction.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import top.nguyennd.restsqlbackend.abstraction.entity.AbstractEntity;

import java.util.List;

@NoRepositoryBean
public interface IBaseRepository<T extends AbstractEntity> extends JpaRepository<T,Long> {

    Page<T> findAll(Specification<T> spec, Pageable pageable);

    List<T> findAll(Specification<T> specification);
}
