package com.ym.asm;

public class TestBean {
	public void halloAop() {
		AopInterceptor.beforeInvoke();
		System.out.println("Hello Aop");
		AopInterceptor.afterInvoke();
	}
}
