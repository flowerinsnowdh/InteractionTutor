package online.flowerinsnow.interactions.redis;

import org.jetbrains.annotations.NotNull;

/**
 * Redis监听器
 */
public interface IRedisListener {
    /**
     * 当接收到一条消息时调用
     *
     * @param from 来自哪个服务器
     * @param service 服务名
     * @param messages 消息
     */
    void onMessage(@NotNull String from, @NotNull String service, @NotNull String... messages);
}
