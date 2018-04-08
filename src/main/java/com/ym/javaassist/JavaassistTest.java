package com.ym.javaassist;

import com.ym.common.domain.User;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.junit.Test;

public class JavaassistTest {

    @Test
    public void modifyExistClass() throws Exception {
        User user = new User();
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("com.ym.common.domain.User");
        cc.setSuperclass(pool.get("java.util.ArrayList"));
        cc.writeFile("target/classes");
        Class clazz =  cc.toClass();
        System.out.println(clazz.getSuperclass());
    }

    @Test
    public void createNewClass() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeInterface("com.ym.common.domain.Point");
        cc.writeFile("target/classes");
    }
}
