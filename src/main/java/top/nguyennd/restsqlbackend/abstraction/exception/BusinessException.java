package top.nguyennd.restsqlbackend.abstraction.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import top.nguyennd.restsqlbackend.abstraction.common.ErrorStatus;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessException extends RuntimeException {

    ErrorStatus errorStatus;

}
