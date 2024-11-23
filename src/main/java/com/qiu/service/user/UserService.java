package com.qiu.service.user;

import com.qiu.pojo.User;

import java.util.List;

public interface UserService {
    //用户登陆
    User login(String userCode, String password);

    //根据用户ID修改密码
    boolean updatePwd(int id, String password);

    //查询记录总数
    int getUserCount(String userName, int userRole);

    //查询用户列表
    List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize);

    //增加用户
    boolean add(User user);

    //根据userCode查询出User
    User selectUserCodeExist(String userCode);

    //根据ID查找user
    User getUserById(String id);

    //修改用户信息
    boolean modify(User user);

    //删除用户信息
    boolean deleteUserById(Integer delId);
}
