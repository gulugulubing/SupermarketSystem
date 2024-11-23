package com.qiu.service.user;

import com.qiu.dao.BaseDao;
import com.qiu.dao.user.UserDao;
import com.qiu.dao.user.UserDaoImpl;
import com.qiu.pojo.User;
import org.junit.Test;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImp implements UserService{
    //业务层都会调用Dao层，所以要引入Dao层,这里是在本地，以后微服务可能是在远程
    //这里userDao相当于service的操作工
    private UserDao userDao;
    public UserServiceImp() {
        userDao = new UserDaoImpl();
    }

    @Override
    public User login(String userCode, String userPassword) {
        Connection connection = null;
        User user = null;

        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }

        //匹配密码
        if(null != user){
            if(!user.getUserPassword().equals(userPassword))
                user = null;
        }
        return user;
    }

    @Override
    public boolean updatePwd(int id, String pwd) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if (userDao.updatePwd(connection, id, pwd) > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    //查询记录数
    @Override
    public int getUserCount(String userName, int userRole) {
        Connection connection = null;
        int count= 0;

        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection, userName, userRole);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return count;
    }

    //这里才是真正获取列表
    @Override
    public List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> users = null;
        System.out.println("queryUserName ---- > " + userName);
        System.out.println("queryUserRole ---- > " + userRole);
        System.out.println("currentPageNo ---- > " + currentPageNo);
        System.out.println("pageSize ---- > " + pageSize);

        try {
            connection = BaseDao.getConnection();
            users = userDao.getUserList(connection, userName, userRole, currentPageNo, pageSize);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection, null, null);
        }
        return users;
    }

    @Override
    public boolean add(User user) {
        int flag = 0;
        Connection connection = null;
        System.out.println("add user ---- > " + user.getUserName());
        try {
            connection = BaseDao.getConnection();
            flag = userDao.add(connection, user);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection, null, null);
        }

        if (flag == 0) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public User selectUserCodeExist(String userCode) {
        Connection connection = null;
        User user = null;
        System.out.println("selectUserCodeExist ---- > " + userCode);
        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }

        return user;
    }

    @Override
    public User getUserById(String id) {
        Connection connection = null;
        User user = null;
        System.out.println("selectUserCodeExist ---- > " + id);
        try {
            connection = BaseDao.getConnection();
            user = userDao.getUserById(connection, id);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }

        return user;
    }

    @Override
    public boolean modify(User user) {
        int flag = 0;
        Connection connection = null;
        System.out.println("modify user ---- > " + user.getUserName());
        try {
            connection = BaseDao.getConnection();
            flag = userDao.modify(connection, user);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection, null, null);
        }

        if (flag == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean deleteUserById(Integer delId) {
        int flag = 0;
        Connection connection = null;
        System.out.println("delete userByID ---- > " + delId);
        try {
            connection = BaseDao.getConnection();
            flag = userDao.deleteUserById(connection, delId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection, null, null);
        }

        if (flag == 0) {
            return false;
        } else {
            return true;
        }
    }

    //登陆用户测试
    @Test
    public void testLogin() {

        UserService userService = new UserServiceImp();
        User admin = userService.login("admin", "1234567");
        System.out.println(admin.getUserPassword());
    }

    //测试查询返回数量
    @Test()
    public void testCount() {
        UserServiceImp userServiceImp = new UserServiceImp();
        int userCount = userServiceImp.getUserCount(null, 0);
        System.out.println(userCount);
    }

}
