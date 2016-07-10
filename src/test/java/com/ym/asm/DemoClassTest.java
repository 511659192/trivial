package com.ym.asm;

import org.junit.Test;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;

public class DemoClassTest {

	public static void main(String[] args) {
        System.out.println("faefafeafe");
    }
	
	@Test
	public void test() throws Exception {
		ClassReader cr = new ClassReader(DemoClassTest.class.getName());
        cr.accept(new DemoClassVisitor(), ClassReader.SKIP_DEBUG);
        System.out.println("---ALL END---");
	}
	
}
class DemoClassVisitor extends ClassVisitor {
	public DemoClassVisitor() {
		super(Opcodes.ASM4);
	}
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		System.out.println("at Method " + name);
		//
		MethodVisitor superMV = super.visitMethod(access, name, desc, signature, exceptions);
		return new DemoMethodVisitor(superMV, name);
	}
}
class DemoMethodVisitor extends MethodVisitor {
	private String methodName;
	public DemoMethodVisitor(MethodVisitor mv, String methodName) {
		super(Opcodes.ASM4, mv);
		this.methodName = methodName;
	}
	public void visitCode() {
		System.out.println("at Method ‘" + methodName + "’ Begin...");
		super.visitCode();
	}
	public void visitEnd() {
		System.out.println("at Method ‘" + methodName + "’End.");
		super.visitEnd();
	}
}
