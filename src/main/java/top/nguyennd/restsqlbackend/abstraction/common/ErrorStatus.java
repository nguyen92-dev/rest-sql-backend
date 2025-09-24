package top.nguyennd.restsqlbackend.abstraction.common;

import lombok.Getter;

@Getter
public enum ErrorStatus {
    NOT_FOUND(404, "Not Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    DATABASE_ERROR(501, "Internal Server Error"),
    CONFLICT(409, "Conflict");

    private final int code;
    private final String message;

    ErrorStatus(int code, String defaultMessage) {
        this.code = code;
        this.message = defaultMessage;
    }
}
