package com.my.study.concurrent;

import org.junit.jupiter.api.Test;

public class ConcurrentTest {

    @Test
    public void userSessionReadTest() throws InterruptedException {
        Thread[] threads = new Thread[10];
        UserSessionRW userSessionRW = new UserSessionRW();

        for (int i = 0; i < threads.length; i++) {
            SessionReadTask sessionReadTask = new SessionReadTask(userSessionRW, new UserSession("session" + i));
            threads[i] = new Thread(sessionReadTask);
        }

        // 중간에 쓰기
        SessionWriteTask sessionWriteTask = new SessionWriteTask(userSessionRW, new UserSession("session" + 4));
        threads[4] = new Thread(sessionWriteTask);

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    public void userSessionWriteTest() throws InterruptedException {
        Thread[] threads = new Thread[10];
        UserSessionRW userSessionRW = new UserSessionRW();

        for (int i = 0; i < threads.length; i++) {
            SessionWriteTask sessionWriteTask = new SessionWriteTask(userSessionRW, new UserSession("session" + i));
            threads[i] = new Thread(sessionWriteTask);
        }

        // 중간에 읽기
        SessionReadTask sessionReadTask = new SessionReadTask(userSessionRW, new UserSession("session" + 4));
        threads[4] = new Thread(sessionReadTask);

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    static class SessionReadTask implements Runnable {
        private UserSessionRW userSession;
        private UserSession session;

        public SessionReadTask(UserSessionRW userSession, UserSession session) {
            this.userSession = userSession;
            this.session = session;
        }

        @Override
        public void run() {
            try {
                userSession.getUserSession(session.getSessionId());
//                System.out.println("userSession = " + session.getSessionId());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class SessionWriteTask implements Runnable {
        private UserSessionRW userSession;
        private UserSession session;

        public SessionWriteTask(UserSessionRW userSession, UserSession session) {
            this.userSession = userSession;
            this.session = session;
        }

        @Override
        public void run() {
            try {
                userSession.addUserSession(session);
//                System.out.println("userSession = " + session.getSessionId());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
