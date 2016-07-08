package com.ym.asm;

import org.junit.Test;

public class TestBean {
	public void halloAop() {
//		AopInterceptor.beforeInvoke();
		System.out.println("Hello Aop");
//		AopInterceptor.afterInvoke();
	}
	
	public void print() {
		System.out.println("bbb");
	}
	
	@Test
	public void test() throws Exception {
		ClassLoader loader = new AopClassLoader(this.getClass().getClassLoader());
		Class clazz = loader.loadClass("com.ym.asm.TestBeanTmp");
		TestBean bean = (TestBean) clazz.newInstance();
		bean.halloAop();
		bean.print();
	}
}
