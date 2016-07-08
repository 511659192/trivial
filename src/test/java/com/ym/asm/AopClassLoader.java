package com.ym.asm;

import java.io.InputStream;

import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.Opcodes;

public class AopClassLoader extends ClassLoader implements Opcodes {
	public AopClassLoader(ClassLoader parent) {
		super(parent);
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (!name.contains("TestBeanTmp"))
			return super.loadClass(name);
		try {
			ClassWriter cw = new ClassWriter(0);
			//
			InputStream is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("com/ym/asm/TestBean.class");
			ClassReader reader = new ClassReader(is);
			reader.accept(new AopClassAdapter(ASM4, cw), ClassReader.SKIP_DEBUG);
			//
			byte[] code = cw.toByteArray();
//			 FileOutputStream fos = new FileOutputStream("c:\\TestBeanTmp.class");
//			 fos.write(code);
//			 fos.flush();
//			 fos.close();
			return this.defineClass(name, code, 0, code.length);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ClassNotFoundException();
		}
	}
}
