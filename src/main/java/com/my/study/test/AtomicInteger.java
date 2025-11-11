package com.my.study.test;

public class AtomicInteger {
    private int value;

//    public void increment() {
//        value++;
//    }

    public synchronized void increment() {
        value++;
    }

    public int get() {
        return value;
    }
}
