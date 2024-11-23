package com.qiu.dao;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.sql.*;
import java.util.Properties;

public class BaseDao {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    //静态代码块，类加载的时候就初始化
    static {
        Properties properties = new Properties();
        InputStream is = BaseDao.class.getClassLoader().
                getResourceAsStream("database.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    //获取数据库的链接
    public static Connection getConnection() {
        Connection connection;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    //编写查询公共方法
    public static ResultSet execute(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet,
                                    String sql, Object[] params) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }

        resultSet = preparedStatement.executeQuery();
        return  resultSet;
    }

    //增删改公共方法
    public static int execute(Connection connection, PreparedStatement preparedStatement,
                              String sql, Object[] params
                              ) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }

        int updateRow = preparedStatement.executeUpdate();
        return  updateRow;
    }

    //释放资源
    public static boolean closeResource(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        boolean flag = true;

        if (resultSet != null) {
            try {
                resultSet.close();
                resultSet = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                preparedStatement = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        return flag;
    }
}
