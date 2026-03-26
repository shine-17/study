package com.my.study.test;

public class VirtualThread {

    public static void main(String[] args) {
        memoryUsages();
        createPlatformThread();
//        createVirtualThread();
        memoryUsages();
    }

    public static void createPlatformThread() {
        int threadCount = 1000;
        Thread[] threads = new Thread[threadCount];
        long start = System.currentTimeMillis();

        for (int i=0; i<threadCount; i++) {
            Thread thread = Thread.ofPlatform().start(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i] = thread;
        }

        long end = System.currentTimeMillis();
        System.out.println("Platform Thread: " + (end - start));
    }

    public static void createVirtualThread() {
        int threadCount = 100_0000;
        Thread[] threads = new Thread[threadCount];
        long start = System.currentTimeMillis();

        for (int i=0; i<threadCount; i++) {
            Thread thread = Thread.ofVirtual().start(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i] = thread;
        }

        long end = System.currentTimeMillis();
        System.out.println("Virtual Thread: " + (end - start));
    }

    public static void memoryUsages() {
        Runtime runtime = Runtime.getRuntime();

        // 1024 * 1024로 나누어 MB 단위로 변환
        long maxMemory = runtime.maxMemory() / (1024 * 1024);     // JVM이 사용할 최대 메모리
        long totalMemory = runtime.totalMemory() / (1024 * 1024); // 현재 할당된 총 메모리
        long freeMemory = runtime.freeMemory() / (1024 * 1024);   // 할당된 메모리 중 여유분
        long usedMemory = totalMemory - freeMemory;              // 실제 사용 중인 메모리

        System.out.println("최대 허용 메모리 (Max): " + maxMemory + " MB");
        System.out.println("현재 할당 메모리 (Total): " + totalMemory + " MB");
        System.out.println("실제 사용 메모리 (Used): " + usedMemory + " MB");
        System.out.println("남은 할당 메모리 (Free): " + freeMemory + " MB");
    }


}
