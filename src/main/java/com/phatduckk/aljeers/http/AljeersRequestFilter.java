package com.phatduckk.aljeers.http;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class AljeersRequestFilter implements Filter {
    public AljeersRequestFilter() {
        super();
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(new AljeersRequest((HttpServletRequest) servletRequest), servletResponse);
    }

    public void destroy() {
    }
}
