package com.example.sqa.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AccessDeniedException.class)
    public void handleConflict(HttpServletResponse response) throws IOException {
        response.sendError(403, "Bạn không có quyền truy cập trang này ");
    }

    @ExceptionHandler(ApiInputException.class)
    public ResponseEntity<String> handleApiInputException(ApiInputException ex) {
        log.warn(String.format("ApiInputException - Response: %s", ex.getError()));
        if (ex.getError().equals("PERMISSION_DENIED")) {
            return new ResponseEntity<>(ex.getError(), HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(ex.getError(), HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        log.error("handleException - ", ex);
        return new ResponseEntity<>("UNKNOWN_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<?> handleException(InternalAuthenticationServiceException ex) {
        log.error("handleException - ", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleException(BadCredentialsException ex) {
        log.error("handleException - ", ex);
        return new ResponseEntity<>("WRONG_ACCOUNT", HttpStatus.BAD_REQUEST);
    }
}
