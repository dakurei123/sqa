package com.example.sqa.controller;

import com.example.sqa.exception.ApiInputException;
import com.example.sqa.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class BaseWebService {
    @Value("${authentication.secret-key}")
    protected String authenSecretKey;

    protected String sub;

    public static final String HEADER_X_API_KEY = "X-api-key";

    public static final String HEADER_AUTHORIZATION = "Authorization";

    public <T> ResponseEntity<T> generateNOK(int httpCode, T data) {
        return ResponseEntity.status(httpCode).body(data);
    }

    protected String getUsername() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String authorizationHeader = request.getHeader(BaseWebService.HEADER_AUTHORIZATION);
        if (authorizationHeader != null && !authorizationHeader.trim().isEmpty()) {
            authorizationHeader = authorizationHeader.trim();
            String jwtToken;
            if (authorizationHeader.startsWith("Bearer ")) jwtToken = authorizationHeader.substring(7);
            else jwtToken = authorizationHeader;
            sub = JwtUtils.getUsernameFromToken(jwtToken, authenSecretKey);
            return sub;
        } else throw new ApiInputException("PERMISSION_DENIED");
    }

    protected String getRole() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String authorizationHeader = request.getHeader(BaseWebService.HEADER_AUTHORIZATION);
        if (authorizationHeader != null && !authorizationHeader.trim().isEmpty()) {
            authorizationHeader = authorizationHeader.trim();
            String jwtToken;
            if (authorizationHeader.startsWith("Bearer ")) jwtToken = authorizationHeader.substring(7);
            else jwtToken = authorizationHeader;
            sub = JwtUtils.getRole(jwtToken, authenSecretKey);
            return sub;
        } else throw new ApiInputException("PERMISSION_DENIED");
    }
}
