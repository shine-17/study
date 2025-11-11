package com.my.study.test;

import java.util.List;

public class ExtendsTest {
    public static void main(String[] args) {
        InstrumentedHashSet<String> set = new InstrumentedHashSet<>();
        set.addAll(List.of("Snap", "Crackle", "Pop"));

        System.out.println("addCount: " + set.getAddCount());
    }
}
