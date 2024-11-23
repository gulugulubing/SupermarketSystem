package com.qiu.servlet.user;

import com.qiu.pojo.User;
import com.qiu.service.user.UserService;
import com.qiu.service.user.UserServiceImp;
import com.qiu.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    //Servlet:控制层调用业务层
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LoginServlet--start...");

        //获取用户名和密码,与前端的name对应
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");

        //和数据库中的密码和用户进行对比，调用业务层
        UserService userService = new UserServiceImp();
        User user = userService.login(userCode, userPassword);

        if (user != null) {
            //将用户信息放入session
            req.getSession().setAttribute(Constants.USER_SESSION, user);
            resp.sendRedirect("jsp/frame.jsp");
        } else {
            //再去请求登陆页面时，带上错误信息
            req.setAttribute("error", "用户名或者密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
