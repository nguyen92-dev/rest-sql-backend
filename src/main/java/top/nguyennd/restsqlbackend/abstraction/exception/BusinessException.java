package top.nguyennd.restsqlbackend.abstraction.exception;

import lombok.Getter;
import top.nguyennd.restsqlbackend.abstraction.common.ErrorStatus;

@Getter
public class BusinessException extends RuntimeException {

    final ErrorStatus errorStatus;

    public BusinessException(ErrorStatus errorStatus, String message) {
        super(message);
        this.errorStatus = errorStatus;
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(ErrorStatus.BAD_REQUEST, message);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(ErrorStatus.NOT_FOUND, message);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(ErrorStatus.CONFLICT, message);
    }

    public static BusinessException forbidden(String message) {
        return  new BusinessException(ErrorStatus.FORBIDDEN, message);
    }

    public static BusinessException internalServerError(String message) {
        return  new BusinessException(ErrorStatus.INTERNAL_SERVER_ERROR, message);
    }
}
