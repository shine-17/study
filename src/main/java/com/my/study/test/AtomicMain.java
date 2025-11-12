package com.my.study.test;

import java.util.ArrayList;
import java.util.List;

public class AtomicMain {
    private static final int THREAD_COUNT = 10;
    private static final AtomicInteger atomicInteger = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        // 1. 싱글 스레드 환경
        // 2. 멀티 스레드 환경
        for (int i=0; i<THREAD_COUNT; i++) {
            Thread thread = new Thread(new AtomicTask());
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("atomicInteger: " + atomicInteger.get());
    }

    static class AtomicTask implements Runnable {
        @Override
        public void run() {
            for (int i=0; i<1000; i++) {
                atomicInteger.increment();
            }
        }
    }
}
