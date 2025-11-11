package com.my.study.test;

public class Member {
    private String name;

    public Member(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Member member = (Member) object;
        return name.equals(member.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
