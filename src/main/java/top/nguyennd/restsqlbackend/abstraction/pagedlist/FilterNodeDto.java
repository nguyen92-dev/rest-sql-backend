package top.nguyennd.restsqlbackend.abstraction.pagedlist;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Data
@Builder
public class FilterNodeDto {
    FilterNodeType filterNodeType;

    List<FilterNodeDto> subFilterNodes;

    FilterOperator filterOperator;

    List<Object> filterValues;
}
