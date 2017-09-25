package com.ym.jmx;

import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Created by yangm on 2017/8/21.
 */
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import com.sun.jdmk.comm.HtmlAdaptorServer;
public class HelloAgent2 {
    public static void main(String args[]) throws Exception{
        MBeanServer server = MBeanServerFactory.createMBeanServer();
        ObjectName helloName = new ObjectName("chengang:name=HelloWorld");
        Hello hello=new Hello();
        server.registerMBean(hello, helloName);
        ObjectName adapterName = new ObjectName("HelloAgent:name=htmladapter,port=8082");
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();
        server.registerMBean(adapter, adapterName);
        Jack jack = new Jack();    //重点
        server.registerMBean(jack, new ObjectName("HelloAgent:name=jack"));    //重点
        jack.addNotificationListener(new HelloListener(), null, hello);    //重点
        adapter.start();
        System.out.println("start.....");
    }
}

