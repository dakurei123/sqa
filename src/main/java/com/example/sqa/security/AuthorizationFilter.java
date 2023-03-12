package com.example.sqa.security;

import com.example.sqa.controller.BaseWebService;
import com.example.sqa.exception.ApiInputException;
import com.example.sqa.exception.BaseException;
import com.example.sqa.service.MyUserDetailsService;
import com.example.sqa.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {
    private final RequestMatcher ignoredPaths = new OrRequestMatcher(
            new AntPathRequestMatcher("/**/login"),
            new AntPathRequestMatcher("/**/signup"),
            new AntPathRequestMatcher("/**/logout")
            );

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"};

    @Value("${authentication.secret-key}")
    protected String authenSecretKey;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            if (this.ignoredPaths.matches(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            if ("OPTIONS".equals(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                String authorizationHeader = request.getHeader(BaseWebService.HEADER_AUTHORIZATION);
                String username = null;
                String jwtToken = null;

                String token;
                if (request.getRequestURL().toString().contains("access-token"))
                    token = authenSecretKey;
                else
                    token = authenSecretKey;

                if (authorizationHeader != null && !authorizationHeader.trim().isEmpty()) {
                    authorizationHeader = authorizationHeader.trim();
                    if (authorizationHeader.startsWith("Bearer "))
                        jwtToken = authorizationHeader.substring(7);
                    else
                        jwtToken = authorizationHeader;
                    username = JwtUtils.getUsernameFromToken(jwtToken, token);
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(username);

                    if (JwtUtils.validateToken(jwtToken, token, userDetails)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }

                log.info("[doFilterInternal] - From: {}, username: {}, url: {}",
                        getClientIpAddress(request), username, request.getRequestURL().toString());
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            log.error("[doFilterInternal] Exception: {}", e.getMessage());
            resolver.resolveException(request, response, null, new ApiInputException("INVALID_TOKEN"));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }

        return request.getRemoteAddr();
    }
}
