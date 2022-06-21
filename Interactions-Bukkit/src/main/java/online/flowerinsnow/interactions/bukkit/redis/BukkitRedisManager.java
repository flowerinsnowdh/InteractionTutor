package online.flowerinsnow.interactions.bukkit.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import online.flowerinsnow.interactions.bukkit.Main;
import online.flowerinsnow.interactions.bukkit.util.IOUtils;
import online.flowerinsnow.interactions.redis.IRedisListener;
import online.flowerinsnow.interactions.redis.IRedisManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BukkitRedisManager implements IRedisManager, RedisPubSubListener<String, String> {
    private static final String SEPC = "/\\";
    private static final String SEPC_REGEX = SEPC.replace("\\", "\\\\");
    private RedisClient client;
    private StatefulRedisPubSubConnection<String, String> subConnection;
    private StatefulRedisPubSubConnection<String, String> connection;
    private final HashSet<String> subscribedServices = new HashSet<>();
    private final ArrayList<IRedisListener> listeners = new ArrayList<>();
    private final String currentName;

    public BukkitRedisManager(String currentName) {
        this.currentName = currentName;
    }

    @Override
    public @NotNull StatefulRedisPubSubConnection<String, String> getConnection() {
        return connection;
    }

    @Override
    public void publish(@NotNull String service, @NotNull String... messages) {
        connection.sync().publish(service, cat(messages));
    }

    @Override
    public void publishAsync(@NotNull String service, @NotNull String... messages) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> publish(service, messages));
    }

    @Override
    public void addListener(@NotNull IRedisListener listener) {
        listeners.add(listener);
    }

    @Override
    public void subscribeService(@NotNull String service) {
        if (!subscribedServices.contains(service)) {
            subConnection.sync().subscribe(service);
            subscribedServices.add(service);
        }
    }

    @Override
    public Set<String> getSubscribedServices() {
        return new HashSet<>(subscribedServices);
    }

    private String cat(String... messages) {
        StringBuilder sb = new StringBuilder(currentName);
        for (String message : messages) {
            sb.append(SEPC).append(message);
        }
        return sb.toString();
    }

    public void connect(String host, int port, @Nullable String password) {
        RedisURI.Builder builder = RedisURI.builder();
        builder.withHost(host).withPort(port);
        if (password != null) {
            builder.withPassword(password.toCharArray());
        }
        RedisURI uri = builder.build();
        client = RedisClient.create(uri);
        subConnection = client.connectPubSub();
        connection = client.connectPubSub();

        subConnection.addListener(this);
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void disconnect() {
        IOUtils.closeQuietly(connection, subConnection);
        connection = subConnection = null;
        if (client != null) {
            client.shutdown();
            client = null;
        }
    }

    @Override
    public @NotNull String getCurrentServerName() {
        return this.currentName;
    }

    @Override
    public void message(String channel, String message) {
        for (IRedisListener listener : listeners) {
            String[] split = message.split(SEPC_REGEX);
            String[] messages = new String[split.length - 1];
            System.arraycopy(split, 1, messages, 0, messages.length);
            listener.onMessage(split[0], channel, messages);
        }
    }

    @Override
    public void message(String pattern, String channel, String message) {
    }

    @Override
    public void subscribed(String channel, long count) {
    }

    @Override
    public void psubscribed(String pattern, long count) {
    }

    @Override
    public void unsubscribed(String channel, long count) {
    }

    @Override
    public void punsubscribed(String pattern, long count) {
    }
}
