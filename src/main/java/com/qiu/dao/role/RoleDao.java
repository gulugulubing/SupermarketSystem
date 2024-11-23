package com.qiu.dao.role;

import java.sql.Connection;
import com.qiu.pojo.Role;
import java.util.List;

public interface RoleDao {
    public List<Role> getRoleList(Connection connection)throws Exception;
}
