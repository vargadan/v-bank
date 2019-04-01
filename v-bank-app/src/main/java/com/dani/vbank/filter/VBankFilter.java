package com.dani.vbank.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class VBankFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String username = ((HttpServletRequest) request).getRemoteUser();
        if (!StringUtils.isEmpty(username)) {
            request.setAttribute("username", username);
        }
        chain.doFilter(request, response);
    }
}
