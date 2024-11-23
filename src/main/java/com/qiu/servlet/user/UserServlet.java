package com.qiu.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import com.qiu.pojo.Role;
import com.qiu.pojo.User;
import com.qiu.service.role.RoleServiceImp;
import com.qiu.service.user.UserServiceImp;
import com.qiu.util.Constants;
import com.qiu.util.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        System.out.println("进入userServlet");
        String method = req.getParameter("method");
        System.out.println(method);
        if (method != null && method.equals("savepwd")) {//修改该密码
            this.updatePwd(req, resp);
        } else if (method != null && method.equals("pwdmodify")) {//这个其实是在验证旧密码
            this.pwdModify(req, resp);
        } else if (method != null && method.equals("query")) {//查询用户列表
            this.query(req, resp);
        } else if (method != null && method.equals("ucexist")) {//这个是suseradd.js检查是否增加用户是否已经存在
            this.userCodeExist(req, resp);
        } else if (method != null && method.equals("add")) {
            this.add(req, resp);
        } else if (method != null && method.equals("deluser")) {//这个是js传来的参数
            this.delUser(req, resp);
        } else if (method != null && method.equals("view")) {
            this.getUserById(req, resp, "userview.jsp"); //因为与method=modify 处理过程类似，只是最后重定向的网页不同
        } else if (method != null && method.equals("modify")) {//这个是js传来的参数
            this.getUserById(req, resp, "usermodify.jsp");//只是进入到modify界面
        } else if (method != null && method.equals("modifyexe")) { //这里才是真正的修改
            this.modify(req, resp);
        } else if (method != null && method.equals("getrolelist")) { //这里usermodify.js里的
            this.getRoleList(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        doGet(req, resp);
    }

    //修改密码
    private void updatePwd(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("进入savepwd");
        //从Session里面那ID,这一层经常用到Session
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        //通过前端的name标签，拿到值,前端的ID是一般是在js css里用
        String newPassword = req.getParameter("newpassword");

        boolean flag = false;
        if (o != null && !StringUtils.isNullOrEmpty(newPassword)) {
            UserServiceImp userServiceImp = new UserServiceImp();
            flag = userServiceImp.updatePwd(((User) o).getId(), newPassword);
            if (flag) {
                req.setAttribute("message", "修改密码成功,请退出,使用新密码登陆");
                req.getSession().removeAttribute(Constants.USER_SESSION);
            } else {
                req.setAttribute("message", "密码修改失败");
            }
        } else {
            req.setAttribute("message", "新密码有问题");
        }
        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //验证旧密码
    private void pwdModify(HttpServletRequest req, HttpServletResponse resp) {
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        //通过前端的name标签，拿到值,前端的ID是一般是在js css里用
        String oldPassword = req.getParameter("oldpassword");

        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (o == null) {
            resultMap.put("result", "sessionError");
        } else if (StringUtils.isNullOrEmpty(oldPassword)) {
            resultMap.put("result", "error");
        } else {
            String userPassword = ((User) o).getUserPassword();
            if (oldPassword.equals(userPassword)) {
                resultMap.put("result", "true");
            } else {
                resultMap.put("result", "false");
            }
        }

        //把查询结果传给前端,通过JSON传递是常见用法
        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //查询用户列表
    private void query(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //从前端获取参数
        //调用service,这里用到Userservice两个方法（一个获取记录总数量，一个获取记录表），还有一个RoleService一个获取角色表
        //通过获取的数据设置请求参数,转发回页面

        //默认值
        List<User> userList = null;
        List<Role> roleList = null;
        int pageSize = Constants.pageSize;
        int currentPageNo = 1;
        int queryUserRole = 0;

        //从前端获取参数
        String queryUserName = req.getParameter("queryname");
        String tmpQueryUserRole = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");

        System.out.println("queryUserName servlet--------"+queryUserName);
        System.out.println("queryUserRole servlet--------"+queryUserRole);
        System.out.println("query pageIndex--------- > " + pageIndex);

        //处理上面的值
        if (tmpQueryUserRole != null && !tmpQueryUserRole.equals("")) {
            queryUserRole = Integer.parseInt(tmpQueryUserRole);
        }
        if(queryUserName == null){
            queryUserName = "";
        }
        if(pageIndex != null){
            try{
                currentPageNo = Integer.valueOf(pageIndex);
            }catch(NumberFormatException e){
                resp.sendRedirect("error.jsp");

            }
        }

        //输入数据处理好了，开始向下层要数据
        UserServiceImp userServiceImp = new UserServiceImp();
        //先要总数据量，处理分页
        //总数量（表）
        int totalCount	= userServiceImp.getUserCount(queryUserName,queryUserRole);
        //总页数
        PageSupport pages=new PageSupport();

        pages.setCurrentPageNo(currentPageNo);

        pages.setPageSize(pageSize);

        pages.setTotalCount(totalCount);

        int totalPageCount = pages.getTotalPageCount();

        //控制首页和尾页
        if(currentPageNo < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }

        //再要数据表列表
        userList = userServiceImp.getUserList(queryUserName,queryUserRole,currentPageNo, pageSize);

        //再要角色表
        RoleServiceImp roleServiceImp = new RoleServiceImp();
        roleList = roleServiceImp.getRoleList();

        //再要角色表
        req.setAttribute("userList", userList);
        req.setAttribute("roleList", roleList);
        req.setAttribute("queryUserName", queryUserName);
        req.setAttribute("queryUserRole", queryUserRole);
        req.setAttribute("totalPageCount", totalPageCount);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);
        req.getRequestDispatcher("userlist.jsp").forward(req, resp);
    }

    private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        System.out.println("add()================");
        User user = new User();
        //id不需要set是数据库自动生成的
        user.setUserCode(req.getParameter("userCode"));
        user.setUserName(req.getParameter("userName"));
        user.setUserPassword(req.getParameter("ruserPassword"));
        user.setGender(Integer.parseInt(req.getParameter("gender")));
        user.setPhone(req.getParameter("phone"));
        user.setAddress(req.getParameter("address"));
        user.setUserRole(Integer.parseInt(req.getParameter("userRole")));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("birthday")));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        UserServiceImp userServiceImp = new UserServiceImp();
        if(userServiceImp.add(user)){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");//成功了，一般是重定向，不带原始数据
        }else{
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);//转发带着原始数据
        }
    }

    private void delUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("delUser()================");
        String id = req.getParameter("uid");//uid是js里data的参数名
        int delId = 0;
        try{
            delId = Integer.parseInt(id);
        }catch (Exception e) {
            // TODO: handle exception
            delId = 0;
        }

        //生成js需要的信息
        HashMap<String, String> resultMap = new HashMap<>();
        if(delId <= 0){
            resultMap.put("delResult", "notexist");
        }else{
            UserServiceImp userService = new UserServiceImp();
            if(userService.deleteUserById(delId)){
                resultMap.put("delResult", "true");
            }else{
                resultMap.put("delResult", "false");
            }
        }

        //把resultMap转换成json对象输出给js处理
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }
    private void userCodeExist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userCode = req.getParameter("userCode");
        HashMap<String, String> resultMap = new HashMap<>();
        if(StringUtils.isNullOrEmpty(userCode)){
            //userCode == null || userCode.equals("")
            resultMap.put("userCode", "exist");
        }else{
            UserServiceImp userService = new UserServiceImp();
            User user = userService.selectUserCodeExist(userCode);
            if(null != user){
                resultMap.put("userCode","exist");
            }else{
                resultMap.put("userCode", "notexist");
            }
        }
        //js里有data.userCode,所以这里要创建一下这个resultMap
        //把resultMap转为json字符串以json的形式输出
        //配置上下文的输出类型
        resp.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter outPrintWriter = resp.getWriter();
        //把resultMap转为json字符串 输出
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }

    private void getUserById(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        String id = req.getParameter("uid");
        if (!StringUtils.isNullOrEmpty(id)) {
            //调用后台方法得到user对象
            UserServiceImp userService = new UserServiceImp();
            User user = userService.getUserById(id);
            req.setAttribute("user", user);//带着user去访问以下页面
            req.getRequestDispatcher(url).forward(req, resp);
        }
    }

    private void modify(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getParameter("uid");
        if (!StringUtils.isNullOrEmpty(id)) {
            UserServiceImp userService = new UserServiceImp();
            User user = userService.getUserById(id);
            user.setUserName(req.getParameter("userName"));
            user.setGender(Integer.parseInt(req.getParameter("gender")));
            user.setPhone(req.getParameter("phone"));
            user.setAddress(req.getParameter("address"));
            user.setUserRole(Integer.parseInt(req.getParameter("userRole")));
            user.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
            user.setModifyDate(new Date());

            try {
                user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("birthday")));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(userService.modify(user)){
                resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");//成功了，一般是重定向，不带原始数据
            }else{//此时的servlet是jsp/user.do，那么这里可以用相对路径
                req.getRequestDispatcher("usermodify.jsp").forward(req, resp);//转发带着原始数据
            }
        }

    }

    private void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoleServiceImp roleServiceImp = new RoleServiceImp();
        List<Role> roleList = roleServiceImp.getRoleList();

        //配置上下文的输出类型
        resp.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(roleList));
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }
}
