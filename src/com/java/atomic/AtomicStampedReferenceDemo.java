package com.java.atomic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * ABA问题的解决
 */
public class AtomicStampedReferenceDemo {

    static AtomicReference<Integer> atomicReference = new AtomicReference<Integer>(100);
    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<Integer>(100, 1);

    public static void main(String[] args) {
        System.out.println("===================以下是ABA问题的产生=====================");
        new Thread(() -> {
            atomicReference.compareAndSet(100, 101);
            atomicReference.compareAndSet(101, 100);
        }, "t1").start();

        new Thread(() -> {
            try {
                // 暂停1秒，保证t1线程完成了一次ABA操作
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.getStackTrace();
            }
            System.out.println(atomicReference.compareAndSet(100, 2019) + "\t" + atomicReference.get());
        }, "t2").start();

        // 等待上面t1、t2都执行完毕
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println("===================以下是ABA问题的解决=====================");
        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t 第1次版本号：" + stamp);
            try {
                // 暂停1秒
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.getStackTrace();
            }
            atomicStampedReference.compareAndSet(100, 101, stamp, stamp + 1);
            System.out.println(Thread.currentThread().getName() + "\t 第2次版本号：" + atomicStampedReference.getStamp());
            atomicStampedReference.compareAndSet(101, 100, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "\t 第3次版本号：" + atomicStampedReference.getStamp());
        }, "t3").start();

        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t 第1次版本号：" + stamp);
            try {
                // 暂停3秒，保证t3线程完成了一次ABA操作
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {
                e.getStackTrace();
            }
            boolean result = atomicStampedReference.compareAndSet(100, 2019, stamp, stamp + 1);
            System.out.println(Thread.currentThread().getName() + "\t 修改成功否：" + result + "\t 当前实际版本号：" + atomicStampedReference.getStamp());
            System.out.println(Thread.currentThread().getName() + "\t 当前实际最新值：" + atomicStampedReference.getReference());
        }, "t4").start();
    }
}
