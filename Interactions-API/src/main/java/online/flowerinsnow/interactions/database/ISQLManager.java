package online.flowerinsnow.interactions.database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

public interface ISQLManager {
    /**
     * 获取一个数据库连接
     *
     * @return 一个数据库连接
     */
    @NotNull Connection getConnection();
}
