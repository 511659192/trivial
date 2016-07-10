package com.ym.asm;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassWriterAopExample {
	public static class Foo {
		public void execute() {
			System.out.println("Hello World");
		}
	}

	public static void main(String[] args) throws Exception {
		Foo foo = new Foo();
		foo.execute();
		ClassReader cr = new ClassReader(Foo.class.getName());
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cr.accept(new ClassAdapter(cw) {
			@Override
			public void visit(final int version, final int access, final String name, final String signature,
					final String superName, final String[] interfaces) {
				cv.visit(version, access, name + "$1", signature, superName, interfaces);
			}

			@Override
			public MethodVisitor visitMethod(final int access, final String name, final String desc,
					final String signature, final String[] exceptions) {
				if ("execute".equals(name)) {
					// 这里只是简单的比较了方法名字，其实还需要比较方法参数，参数信息在desc中
					return cv.visitMethod(access, name + "$1", desc, signature, exceptions);
				}
				return cv.visitMethod(access, name, desc, signature, exceptions);
			}

		}, 0);
		// 到这里，如果调用cr.toByteArray，生成的字节码中，已经没有execute方法了，而是execute$1

		// 我们接着需要增加一个execute方法
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "execute", "()V", null, null);
		// 开始增加代码
		mv.visitCode();
		// 接下来，我们需要把新的execute方法的内容，增加到这个方法中
		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn("Before execute");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/vanadies/bytecode/example/asm3/ClassWriterAopExample$Foo$1",
				"execute$1", "()V");
		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn("End execute");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0); // 这个地方，最大的操作数栈和最大的本地变量的空间，是自动计算的，是因为构造ClassWriter的时候使用了ClassWriter.COMPUTE_MAXS
		mv.visitEnd();
		// 到这里，就完成了execute方法的添加。
		// 可以把字节码写入到文件，用javap -c，来看下具体的内容
	}
}
