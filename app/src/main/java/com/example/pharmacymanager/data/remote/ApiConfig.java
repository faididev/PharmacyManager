package com.example.pharmacymanager.data.remote;

public final class ApiConfig {
    private ApiConfig() {}

    // Use 10.0.2.2 for Android emulator to reach host machine's localhost.
    public static final String BASE_URL = "http://192.168.1.102:8080/api/v1/";

    public static String buildUrl(String path) {
        if (path == null || path.isEmpty()) {
            return BASE_URL;
        }

        // Return absolute URLs unchanged
        String lower = path.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            return path;
        }

        String base = BASE_URL;
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        String cleanedPath = path;
        if (cleanedPath.startsWith("/")) {
            cleanedPath = cleanedPath.substring(1);
        }
        return base + "/" + cleanedPath;
    }
}



