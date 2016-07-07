package com.ym.asm;

import java.io.Serializable;
import java.util.ArrayList;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Hello extends ArrayList<String> implements Serializable  {

	@Test
	public void halloAop() {
		System.out.println("before");
		System.out.println("Hello Aop");
		System.out.println("after");
	}

	@Autowired
	private String name;

	private static String love;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static String getLove() {
		return love;
	}

	public static void setLove(String love) {
		Hello.love = love;
	}
}
