package online.flowerinsnow.interactions.bukkit.database;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import online.flowerinsnow.interactions.bukkit.util.IOUtils;
import online.flowerinsnow.interactions.database.ISQLManager;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class BukkitSQLManager implements ISQLManager {
    private DruidDataSource dataSource;

    /**
     * 建立与数据库的连接
     *
     * @param url 连接地址
     * @param username 用户名
     * @param password 密码
     * @param initSize 初始连接数
     * @param maxActive 最大连接数
     * @param maxWait 最多等待时间
     * @return 是否成功连接
     */
    public boolean connect(String url, String username, String password, int initSize, int maxActive, long maxWait) {
        Properties properties = new Properties();
        properties.setProperty(DruidDataSourceFactory.PROP_URL, url);
        properties.setProperty(DruidDataSourceFactory.PROP_USERNAME, username);
        properties.setProperty(DruidDataSourceFactory.PROP_PASSWORD, password);
        properties.setProperty(DruidDataSourceFactory.PROP_INITIALSIZE, Integer.toString(initSize));
        properties.setProperty(DruidDataSourceFactory.PROP_MAXACTIVE, Integer.toString(maxActive));
        properties.setProperty(DruidDataSourceFactory.PROP_MAXWAIT, Long.toString(maxWait));
        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 是否已连接到数据库
     *
     * @return 已连接到数据库
     */
    public boolean isConnected() {
        return dataSource != null;
    }

    public void disconnect() {
        IOUtils.closeQuietly(dataSource);
        dataSource = null;
    }

    @Override
    public @NotNull Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
