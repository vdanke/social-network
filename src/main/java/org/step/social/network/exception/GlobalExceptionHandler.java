package org.step.social.network.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        Map<String, String> exceptionMap = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> {
                            String defaultMessage = fieldError.getDefaultMessage();

                            if (!StringUtils.isEmpty(defaultMessage)) {
                                return defaultMessage;
                            } else {
                                return "Something went wrong";
                            }
                        }
                ));
        exceptionMap.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.badRequest()
                .headers(headers)
                .body(exceptionMap);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(NotFoundException e, WebRequest request) {
        return new ResponseEntity<>(formExceptionResponse(e, request), HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> formExceptionResponse(Exception e, WebRequest request) {
        Map<String, Object> exceptionMap = new HashMap<>();

        exceptionMap.put("cause", e.getCause());
        exceptionMap.put("message", e.getLocalizedMessage());
        exceptionMap.put("timestamp", LocalDateTime.now());
        exceptionMap.put("path", request.getContextPath());

        return exceptionMap;
    }
}
