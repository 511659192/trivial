package com.ym.asm;

import java.util.*;
import org.objectweb.asm.*;

public class TestBeanDump implements Opcodes {

	public static byte[] dump() throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, "com/ym/asm/TestBean", null, "java/lang/Object", null);

		cw.visitSource("TestBean.java", null);

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "halloAop", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitMethodInsn(INVOKESTATIC, "com/ym/asm/AopInterceptor", "beforeInvoke", "()V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitLdcInsn("Hello Aop");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitMethodInsn(INVOKESTATIC, "com/ym/asm/AopInterceptor", "afterInvoke", "()V", false);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitMaxs(2, 1);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
