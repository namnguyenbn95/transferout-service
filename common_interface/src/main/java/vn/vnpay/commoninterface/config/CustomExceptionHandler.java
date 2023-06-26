package vn.vnpay.commoninterface.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.response.BaseClientResponse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.info(ex.getLocalizedMessage(), ex);
        List<String> details = new ArrayList<String>();
        for (ObjectError error : ex.getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        BaseClientResponse<Object> response = new BaseClientResponse<Object>(Constants.ResCode.ERROR_95, "Validation Failed", details);
        return new ResponseEntity<Object>(response, status);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        log.info(ex.getLocalizedMessage(), ex);
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        BaseClientResponse<Object> response = new BaseClientResponse<Object>(Constants.ResCode.ERROR_96, "Internal Server Error", details);
        return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
