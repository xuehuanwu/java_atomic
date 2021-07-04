package com.java.atomic;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerDemo {

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(5);
        System.out.println(atomicInteger.compareAndSet(5, 2019) + "\t current data: " + atomicInteger.get());
        System.out.println(atomicInteger.compareAndSet(5, 1024) + "\t current data: " + atomicInteger.get());
        /**
         * 调用unsafe.getAndAddInt(this, valueOffset, 1);
         * this：当前对象
         * valueOffset：内存地址偏移量
         */
        atomicInteger.getAndIncrement();
    }
}
