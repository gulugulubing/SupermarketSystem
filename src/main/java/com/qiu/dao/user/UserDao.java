package com.qiu.dao.user;

import com.qiu.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    //得到要登陆的用户
    User getLoginUser(Connection connection, String userCode) throws SQLException;

    //修改当期用户密码(增删改都是返回int)
    int updatePwd(Connection connection, int id, String password) throws SQLException;

    //查询用户总数
    int getUserCount(Connection connection, String userName, int userRole) throws SQLException;
    //获取用户列表

    List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws SQLException;

    //增加用户
    int add(Connection connection, User user)throws Exception;
    //删除用户
    int deleteUserById(Connection connection, Integer delId)throws Exception;
    //获取用户
    User getUserById(Connection connection, String id)throws Exception;
    //修改用户
    int modify(Connection connection, User user)throws Exception;
}
