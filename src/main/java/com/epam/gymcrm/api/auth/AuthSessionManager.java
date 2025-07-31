package com.epam.gymcrm.api.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthSessionManager {
    private static final Map<String, Boolean> serssionMap = new ConcurrentHashMap<>();

    public static void login(String username) {
        serssionMap.put(username, true);
    }

    public static boolean isLoggedInd(String username) {
        return serssionMap.getOrDefault(username, false);
    }

    public static void logout(String username) {
        serssionMap.remove(username);
    }
}
