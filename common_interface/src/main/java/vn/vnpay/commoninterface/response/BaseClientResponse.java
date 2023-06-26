package vn.vnpay.commoninterface.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.MDC;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public final class BaseClientResponse<T> {

    private String code;
    private String message;
    private String traceId = MDC.get("traceId");
    private List<String> details;
    private T data;

    public BaseClientResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseClientResponse(String code, String message, List<String> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }
}
