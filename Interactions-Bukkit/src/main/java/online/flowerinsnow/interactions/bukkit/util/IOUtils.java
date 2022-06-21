package online.flowerinsnow.interactions.bukkit.util;

public class IOUtils {
    public static void closeQuietly(AutoCloseable... acs) {
        for (AutoCloseable ac : acs) {
            try {
                if (ac != null) ac.close();
            } catch (Exception ignored) {
            }
        }
    }
}
