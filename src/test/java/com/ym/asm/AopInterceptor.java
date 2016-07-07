package com.ym.asm;

public class AopInterceptor {
	public static void beforeInvoke() {
		System.out.println("before");
	};

	public static void afterInvoke() {
		System.out.println("after");
	};
}
