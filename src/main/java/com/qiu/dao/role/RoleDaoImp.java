package com.qiu.dao.role;

import com.qiu.dao.BaseDao;
import com.qiu.pojo.Role;
import com.qiu.pojo.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImp implements RoleDao {
    @Override
    public List<Role> getRoleList(Connection connection) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Role> roles = new ArrayList<>();
        if (connection != null) {
            String sql = "select * from smb_role";
            Object[] paras = {};
            rs = BaseDao.execute(connection, pstm, rs, sql, paras);
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setRoleCode(rs.getString("roleCode"));
                role.setRoleName(rs.getString("roleName"));
                roles.add(role);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return roles;
    }
}
