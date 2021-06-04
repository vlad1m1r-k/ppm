package com.vladimir.ppm.filter;

import com.vladimir.ppm.provider.SecurityProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class IpFilter implements Filter {
    private final SecurityProvider securityProvider;

    public IpFilter(SecurityProvider securityProvider) {
        this.securityProvider = securityProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (securityProvider.isIpBanned(servletRequest.getRemoteAddr())) {
            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_FORBIDDEN);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
