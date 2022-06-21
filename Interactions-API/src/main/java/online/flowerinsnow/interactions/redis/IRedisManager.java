package online.flowerinsnow.interactions.redis;

import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Redis管理器
 */
public interface IRedisManager {
    /**
     * 获取Redis连接
     *
     * @return Redis连接
     */
    @NotNull StatefulRedisPubSubConnection<String, String> getConnection();

    /**
     * 公布一条消息，订阅此服务的终端可以接收到这条消息
     * 因为涉及到IO 所以调用此方法请异步
     *
     * @param service 服务名
     * @param messages 消息
     */
    void publish(@NotNull String service, @NotNull String... messages);

    /**
     * 异步公布一条消息，订阅此服务的终端可以接收到这条消息
     *
     * @see IRedisManager#publish(String, String...)
     */
    void publishAsync(@NotNull String service, @NotNull String... messages);

    /**
     * 添加一个监听器，请重写IRedisListener
     *
     * @param listener 监听器
     */
    void addListener(@NotNull IRedisListener listener);

    /**
     * 在此服务器上订阅一个服务
     *
     * @param service 服务名
     */
    void subscribeService(@NotNull String service);

    /**
     * 获取已订阅的所有服务
     *
     * @return 已订阅的所有服务
     */
    Set<String> getSubscribedServices();

    /**
     * 获取当前服务器名
     *
     * @return 当前服务器名
     */
    @NotNull String getCurrentServerName();
}
