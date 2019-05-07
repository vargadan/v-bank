package com.dani.vbank;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

//@Component
@Order(1)
public class CsrfFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        validate((HttpServletRequest) request, (HttpServletResponse) response);
        chain.doFilter(request, response);
        setToken((HttpServletRequest) request, (HttpServletResponse) response);
    }

    private void validate(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if ("POST".equals(request.getMethod().toUpperCase())) {
            String csrfTokenIn = request.getParameter("_csrf");
            String csrfTokenExp = (String) request.getSession().getAttribute("csrfToken");
            if (!csrfTokenExp.equals(csrfTokenIn)) {
                throw new ServletException("Invalid CSRF token!");
            }
        }
    }

    private void setToken(HttpServletRequest request, HttpServletResponse response) {
        String csrfToken = (String) request.getSession(true).getAttribute("csrfToken");
        if (csrfToken == null) {
            csrfToken = UUID.randomUUID().toString();
            request.getSession().setAttribute("csrfToken", csrfToken);
        }
    }
}
