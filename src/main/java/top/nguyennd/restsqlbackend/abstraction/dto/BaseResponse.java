package top.nguyennd.restsqlbackend.abstraction.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseResponse<T> {
    T data;
    String message;
    int status;

    public static <T> BaseResponse<T> buildSuccess(T data) {
        return BaseResponse.<T>builder()
                .data(data)
                .status(200)
                .message("success")
                .build();
    }
}
