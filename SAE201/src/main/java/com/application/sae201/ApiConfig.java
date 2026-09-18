package com.application.sae201;

public final class ApiConfig {

    private static final String ENV_VAR = "RAWG_API_KEY";
    private static final String SYSTEM_PROPERTY = "rawg.api.key";

    private ApiConfig() {
    }

    public static String getRawgApiKey() {
        String fromEnv = System.getenv(ENV_VAR);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }

        String fromProperty = System.getProperty(SYSTEM_PROPERTY);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty.trim();
        }

        return null;
    }

    public static boolean isRawgApiAvailable() {
        return getRawgApiKey() != null;
    }
}
