package com.qiu.service.role;

import com.qiu.dao.BaseDao;
import com.qiu.dao.role.RoleDaoImp;
import com.qiu.pojo.Role;

import java.sql.Connection;
import java.util.List;

public class RoleServiceImp implements RoleService{
    private RoleDaoImp roleDaoImp;
    public RoleServiceImp(){
        roleDaoImp = new RoleDaoImp();
    }


    @Override
    public List<Role> getRoleList() {
        Connection connection = null;
        List<Role> users = null;
        try {
            connection = BaseDao.getConnection();
            users = roleDaoImp.getRoleList(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return users;
    }
}
