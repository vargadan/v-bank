package com.dani.vbank.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class SessionInvalidatorOnErrorFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            log.warn("There seems to have been an exception; invalidating session : " + e.getMessage());
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            if (session != null) {
                session.invalidate();
            }
            throw e;
        }
    }

}
