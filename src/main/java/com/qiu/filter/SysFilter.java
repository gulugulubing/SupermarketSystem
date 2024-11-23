package com.qiu.filter;

import com.qiu.pojo.User;
import com.qiu.util.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //为了获取seesion
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //为了重定向
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }


    }

    @Override
    public void destroy() {

    }
}
