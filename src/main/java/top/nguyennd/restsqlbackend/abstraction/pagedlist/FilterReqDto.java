package top.nguyennd.restsqlbackend.abstraction.pagedlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterReqDto {

    Map<String, FilterNodeDto> filters;
}
