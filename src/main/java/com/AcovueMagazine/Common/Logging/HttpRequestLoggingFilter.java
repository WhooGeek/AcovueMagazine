package com.AcovueMagazine.Common.Logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsedMs = System.currentTimeMillis() - start;
            String queryString = request.getQueryString();
            String path = queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString;

            log.info("[HTTP] {} {} -> {} ({}ms)",
                    request.getMethod(),
                    path,
                    response.getStatus(),
                    elapsedMs);
        }
    }
}
