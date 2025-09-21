package top.nguyennd.restsqlbackend.abstraction.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.nguyennd.restsqlbackend.abstraction.common.ErrorStatus;
import top.nguyennd.restsqlbackend.abstraction.dto.BaseResponse;

import java.sql.SQLException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    BaseResponse<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: ", ex);
        return BaseResponse.<Void>builder()
                .status(ErrorStatus.BAD_REQUEST.getCode())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    BaseResponse<Void> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException: ", ex);
        return BaseResponse.<Void>builder()
                .status(ErrorStatus.INTERNAL_SERVER_ERROR.getCode())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(SQLException.class)
    BaseResponse<Void> handleSQLException(SQLException ex) {
        log.error("SQLException: ", ex);
        return BaseResponse.<Void>builder()
                .status(ErrorStatus.DATABASE_ERROR.getCode())
                .message("Database error occurred")
                .build();
    }

    @ExceptionHandler(BusinessException.class)
    BaseResponse<Void> handleBusinessException(BusinessException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : ex.getErrorStatus().getMessage();
        log.error("BusinessException: ", ex);
        return BaseResponse.<Void>builder()
                .status(ex.getErrorStatus().getCode())
                .message(message)
                .build();
    }
}
