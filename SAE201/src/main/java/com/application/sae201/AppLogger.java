package com.application.sae201;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AppLogger {

    private static final Logger LOGGER = Logger.getLogger("com.application.sae201");

    static {
        for (var handler : Logger.getLogger("").getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                try {
                    handler.setEncoding("UTF-8");
                } catch (java.io.UnsupportedEncodingException ignored) {
                }
            }
        }
    }

    private AppLogger() {
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warn(String message) {
        LOGGER.warning(message);
    }

    public static void error(String message, Throwable t) {
        LOGGER.log(Level.SEVERE, message, t);
    }

    public static void error(String message) {
        LOGGER.severe(message);
    }
}
