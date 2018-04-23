package com.ym.asm;

import org.objectweb.asm.*;
public class AsmTestDump implements Opcodes {

    public static byte[] dump () throws Exception {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, "com/ym/asm/AsmTest", null, "java/lang/Object", null);

        cw.visitInnerClass("java/util/Map$Entry", "java/util/Map", "Entry", ACC_PUBLIC + ACC_STATIC + ACC_ABSTRACT + ACC_INTERFACE);

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "set", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/util/List;Ljava/util/Set;Ljava/util/Map;Ljava/lang/Object;)Ljava/lang/Integer;", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/util/List<Ljava/lang/Integer;>;Ljava/util/Set<Ljava/lang/Integer;>;Ljava/util/Map<Ljava/lang/Integer;Ljava/lang/Integer;>;Ljava/lang/Object;)Ljava/lang/Integer;", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ASTORE, 7);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IADD);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ASTORE, 8);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitInsn(POP);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ASTORE, 7);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;");
            mv.visitVarInsn(ASTORE, 8);
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {"java/lang/String", "java/util/Iterator"}, 0, null);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
            Label l1 = new Label();
            mv.visitJumpInsn(IFEQ, l1);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitVarInsn(ASTORE, 9);
            mv.visitJumpInsn(GOTO, l0);
            mv.visitLabel(l1);
            mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "entrySet", "()Ljava/util/Set;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;");
            mv.visitVarInsn(ASTORE, 8);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {"java/util/Iterator"}, 0, null);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
            Label l3 = new Label();
            mv.visitJumpInsn(IFEQ, l3);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, "java/util/Map$Entry");
            mv.visitVarInsn(ASTORE, 9);
            mv.visitJumpInsn(GOTO, l2);
            mv.visitLabel(l3);
            mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
            mv.visitVarInsn(ALOAD, 6);
            Label l4 = new Label();
            mv.visitJumpInsn(IFNULL, l4);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
            mv.visitVarInsn(ASTORE, 7);
            mv.visitLabel(l4);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 10);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
