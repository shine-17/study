package com.my.study.test;

public class Main {
    public static void main(String[] args) {
        Member member1 = new Member("test");
        Member member2 = member1;
        Member member3 = new Member("test");

        // 동일성 비교
        boolean identity = member1 == member2;
        System.out.println(identity); // true

        // 동등성 비교
        boolean equals = member1.equals(member3);
        System.out.println(equals); // true

        System.out.println(member1.hashCode());
        System.out.println(member2.hashCode());


        // 문자열 비교
        String str1 = "hello";
        String str2 = "hello";
        String str3 = new String("hello");

        System.out.println(str1 == str2);       // true
        System.out.println(str1 == str3);       // false
        System.out.println(str1.equals(str3));  // true
    }

    static class Member {
        private String name;

        public Member(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object object) {
            Member member = (Member) object;
            return name.equals(member.name);
        }
    }
}
