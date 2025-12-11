package com.my.study.concurrent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserSessionRW {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writelock = lock.writeLock();
    private final Lock readlock = lock.readLock();
    private final Map<String, UserSession> sessions = new HashMap<>();

    // Exclusive Lock
    public void addUserSession(UserSession session) throws InterruptedException {
        writelock.lock();

        Thread.sleep(3000);
        System.out.println(LocalDateTime.now() + " 쓰기 접근. sessionId: " + session.getSessionId());

        try {
            sessions.put(session.getSessionId(), session);
        } finally {
            writelock.unlock();
        }
    }

    // Shared Lock
    public UserSession getUserSession(String sessionId) throws InterruptedException {
        readlock.lock();

        Thread.sleep(3000);
        System.out.println(LocalDateTime.now() + " 읽기 접근. sessionId: " + sessionId);

        try {
            return sessions.get(sessionId);
        } finally {
            readlock.unlock();
        }
    }
}
