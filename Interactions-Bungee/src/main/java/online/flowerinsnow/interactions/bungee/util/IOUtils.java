package online.flowerinsnow.interactions.bungee.util;

import java.io.*;

public class IOUtils {
    public static void closeQuietly(AutoCloseable... acs) {
        for (AutoCloseable ac : acs) {
            try {
                if (ac != null) ac.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static void copy(InputStream in, boolean autoCloseIn, OutputStream out, boolean autoCloseOut) {
        try {
            in.transferTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (autoCloseIn) IOUtils.closeQuietly(in);
            if (autoCloseOut) IOUtils.closeQuietly(out);
        }
    }

    public static void copy(InputStream in, boolean autoCloseIn, File out) {
        try {
            if (!out.exists()) out.createNewFile();
            copy(in, autoCloseIn, new FileOutputStream(out), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
