package com.my.study.concurrent;

public class UserSession {
    private final String id;

    public String getSessionId() {
        return id;
    }

    public UserSession(String id) {
        this.id = id;
    }
}
