package com.qiu.dao.provider;

import com.mysql.jdbc.StringUtils;
import com.qiu.dao.BaseDao;
import com.qiu.pojo.Provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProvideDaoImp implements ProvideDao{
    @Override
    public int add(Connection connection, Provider provider) throws Exception {
        PreparedStatement pstm = null;
        int updateRow = 0;

        String proCode = provider.getProCode();	//供应商编码
        String proName = provider.getProName(); //供应商名称
        String proDesc = provider.getProDesc(); //供应商描述
        String proContact = provider.getProContact(); //供应商联系
        String proPhone = provider.getProPhone(); //供应商电话
        String proAddress = provider.getProAddress(); //供应商地址
        String proFax = provider.getProFax(); //供应商传真
        Integer createdBy = provider.getCreatedBy(); //创建者
        Date creationDate = provider.getCreationDate(); //创建时间

        if(connection != null) {
            String sql = "insert into smb_provider (proCode,proName,proDesc," +
                    "proContact,proPhone,proAddress,proFax,createdBy,creationDate) " +
                    "values(?,?,?,?,?,?,?,?,?)";
            Object[] params = {proCode,proName,proDesc,
                    proContact,proPhone,proAddress,
                    proFax,createdBy,creationDate};
            updateRow = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRow;
    }

    @Override
    public List<Provider> getProviderList(Connection connection, String proName, String proCode) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Provider> providerList = new ArrayList<Provider>();
        if(connection != null){
            StringBuffer sql = new StringBuffer();

            //1=1是一种拼接习惯写法，下面就直接and拼接各种其他的sql
            sql.append("select * from smb_provider where 1=1 ");
            List<Object> list = new ArrayList<Object>();
            if(!StringUtils.isNullOrEmpty(proName)){
                sql.append(" and proName like ?");
                list.add("%"+proName+"%");
            }
            if(!StringUtils.isNullOrEmpty(proCode)){
                sql.append(" and proCode like ?");
                list.add("%"+proCode+"%");
            }
            Object[] params = list.toArray();
            System.out.println("sql ----> " + sql.toString());
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            while(rs.next()){
                Provider _provider = new Provider();
                _provider.setId(rs.getInt("id"));
                _provider.setProCode(rs.getString("proCode"));
                _provider.setProName(rs.getString("proName"));
                _provider.setProDesc(rs.getString("proDesc"));
                _provider.setProContact(rs.getString("proContact"));
                _provider.setProPhone(rs.getString("proPhone"));
                _provider.setProAddress(rs.getString("proAddress"));
                _provider.setProFax(rs.getString("proFax"));
                _provider.setCreationDate(rs.getTimestamp("creationDate"));
                providerList.add(_provider);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return providerList;
    }

    @Override
    public int deleteProviderById(Connection connection, String delId) throws Exception {
        PreparedStatement pstm = null;
        int flag = 0;
        if(connection != null){
            if(null != connection){
                String sql = "delete from smb_provider where id=?";
                Object[] params = {delId};
                flag = BaseDao.execute(connection, pstm, sql, params);
                BaseDao.closeResource(null, pstm, null);
            }
            return flag;
        }
        return 0;
    }

    @Override
    public Provider getProviderById(Connection connection, String id) throws Exception {
        PreparedStatement pstm = null;
        Provider provider = null;
        ResultSet rs = null;
        if(null != connection) {
            String sql = "select * from smb_provider where id=?";
            Object[] params = {id};
            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            if (rs.next()) {
                provider = new Provider();
                provider.setId(rs.getInt("id"));
                provider.setProCode(rs.getString("proCode"));
                provider.setProName(rs.getString("proName"));
                provider.setProDesc(rs.getString("proDesc"));
                provider.setProContact(rs.getString("proContact"));
                provider.setProPhone(rs.getString("proPhone"));
                provider.setProAddress(rs.getString("proAddress"));
                provider.setProFax(rs.getString("proFax"));
                provider.setCreatedBy(rs.getInt("createdBy"));
                provider.setCreationDate(rs.getTimestamp("creationDate"));
                provider.setModifyBy(rs.getInt("modifyBy"));
                provider.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return provider;
    }

    @Override
    public int modify(Connection connection, Provider provider) throws Exception {
        PreparedStatement pstm = null;
        int updateRow = 0;

        String proName = provider.getProName(); //供应商名称
        String proDesc = provider.getProDesc(); //供应商描述
        String proContact = provider.getProContact(); //供应商联系
        String proPhone = provider.getProPhone(); //供应商电话
        String proAddress = provider.getProAddress(); //供应商地址
        String proFax = provider.getProFax(); //供应商传真
        Integer modifyBy = provider.getModifyBy(); //创建者
        Date modifyDate = provider.getModifyDate(); //创建时间

        String sql = "update smb_provider set proName=?,proDesc=?,proContact=?," +
                "proPhone=?,proAddress=?,proFax=?,modifyBy=?,modifyDate=? where id = ? ";
        Object[] params = {proName,proDesc,proContact,proPhone,proAddress,
                proFax,modifyBy,modifyDate,provider.getId()};
        updateRow = BaseDao.execute(connection, pstm, sql, params);
        BaseDao.closeResource(null, pstm, null);
        return updateRow;
    }
}
