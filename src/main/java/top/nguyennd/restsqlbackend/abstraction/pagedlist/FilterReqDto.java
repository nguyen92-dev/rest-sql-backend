package top.nguyennd.restsqlbackend.abstraction.pagedlist;

import lombok.Data;

import java.util.Map;

@Data
public class FilterReqDto {

    Map<String, FilterNodeDto> filters;
}
