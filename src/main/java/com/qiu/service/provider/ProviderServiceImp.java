package com.qiu.service.provider;

import com.qiu.dao.BaseDao;
import com.qiu.dao.provider.ProvideDao;
import com.qiu.dao.provider.ProvideDaoImp;
import com.qiu.pojo.Provider;

import java.sql.Connection;
import java.util.List;

public class ProviderServiceImp implements ProviderService {
    private ProvideDao providerDao;

    public ProviderServiceImp() {
        providerDao = new ProvideDaoImp();
    }

    @Override
    public boolean add(Provider provider) {
        Connection connection = null;
        int flag = 0;
        try {
            connection = BaseDao.getConnection();
            flag = providerDao.add(connection, provider);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flag == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<Provider> getProviderList(String proName, String proCode) {
        Connection connection = null;
        List<Provider> providerList = null;
        try {
            connection = BaseDao.getConnection();
            providerList = providerDao.getProviderList(connection, proName, proCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return providerList;
    }

    @Override
    public int deleteProviderById(String delId) {
        Connection connection = null;
        int updateRow  = 0;
        try {
            connection = BaseDao.getConnection();
            updateRow = providerDao.deleteProviderById(connection, delId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return updateRow;
    }

    @Override
    public Provider getProviderById(String id) {
        Connection connection = null;
        Provider provider = null;
        try {
            connection = BaseDao.getConnection();
            provider = providerDao.getProviderById(connection, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return provider;
    }

    @Override
    public boolean modify(Provider provider) {
        Connection connection = null;
        int flag = 0;
        try {
            connection = BaseDao.getConnection();
            flag = providerDao.modify(connection, provider);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flag == 0) {
            return false;
        } else {
            return true;
        }
    }
}
