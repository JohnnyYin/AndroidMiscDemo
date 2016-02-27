package com.johnnyyin.temp;

import java.lang.ref.WeakReference;

public class Test {
	private static A a;
	private static Thread t;
	private static Runnable r;

	private static WeakReference<A> refA;
	private static WeakReference<Thread> refT;
	private static WeakReference<Runnable> refR;

	public static class A {
		public void FunB() {
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					System.out.println("run");
				}
			};
			Thread thread = new Thread(runnable);
			thread.start();
			refT = new WeakReference<Thread>(thread);
			//t = thread;// 测试Thread被静态引用
			thread = null;
			refR = new WeakReference<Runnable>(runnable);
			//r = runnable;// 测试Runnable被静态引用
			runnable = null;
		}

		public static void test() {
			A p = new A();
			p.FunB();
			refA = new WeakReference<Test.A>(p);
			//a = p;// 测试A被静态引用
		}
	}

	public static void main(String[] args) {
		A.test();
		while (true) {
			A a = refA.get();
			Thread t = refT.get();
			Runnable r = refR.get();
			System.out.println("a = " + a + ", t = " + t + ", r = " + r);
			if (a == null && t == null && r == null) {
				break;
			}
			a = null;
			t = null;
			r = null;
			System.gc();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}