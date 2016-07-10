package com.ym.asm;

import java.io.InputStream;

import org.junit.Test;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;

public class ExtendTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void test() throws Exception {
		ClassLoader loader = new AopClassLoader(this.getClass().getClassLoader());
		Class clazz = loader.loadClass("com.ym.asm.TestBeanTmp");
		TestBean bean = (TestBean) clazz.newInstance();
		bean.halloAop();
	}

	class AopClassLoader extends ClassLoader implements Opcodes {
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
				// FileOutputStream fos = new
				// FileOutputStream("c:\\TestBeanTmp.class");
				// fos.write(code);
				// fos.flush();
				// fos.close();
				return this.defineClass(name, code, 0, code.length);
			} catch (Throwable e) {
				e.printStackTrace();
				throw new ClassNotFoundException();
			}
		}
	}

	class AopClassAdapter extends ClassVisitor implements Opcodes {
		public AopClassAdapter(int api, ClassVisitor cv) {
			super(api, cv);
		}

		@SuppressWarnings("deprecation")
		public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {
			// 更改类名，并使新类继承原有的类。
			super.visit(version, access, name + "Tmp", signature, name, interfaces);
			{// 输出一个默认的构造方法
				MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, name, "<init>", "()V");
				mv.visitInsn(RETURN);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
		}

		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			if ("<init>".equals(name))
				return null;// 放弃原有类中所有构造方法
			if (!name.equals("halloAop"))
				return null;// 只对halloAop方法执行代理
			//
			MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
			return new AopMethod(this.api, mv);
		}
	}

	class AopMethod extends MethodVisitor implements Opcodes {
		public AopMethod(int api, MethodVisitor mv) {
			super(api, mv);
		}

		@SuppressWarnings("deprecation")
		public void visitCode() {
			super.visitCode();
			this.visitMethodInsn(INVOKESTATIC, "com/ym/asm/AopInterceptor", "beforeInvoke", "()V");
		}

		@SuppressWarnings("deprecation")
		public void visitInsn(int opcode) {
			if (opcode == RETURN) {// 在返回之前安插after 代码。
				mv.visitMethodInsn(INVOKESTATIC, "com/ym/asm/AopInterceptor", "afterInvoke", "()V");
			}
			super.visitInsn(opcode);
		}
	}
}
