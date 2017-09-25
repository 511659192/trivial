package com.ym.jmx;

import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

/**
 * Created by yangm on 2017/8/21.
 */

public class HelloAgentDynamic {
    public static void main(String[] args) throws Exception {
        MBeanServer server = MBeanServerFactory.createMBeanServer();
        ObjectName helloName = new ObjectName("chengang:name=HelloDynamic");
        HelloDynamic hello = new HelloDynamic();
        server.registerMBean(hello, helloName);
        ObjectName adapterName = new ObjectName("HelloAgent:name=htmladapter,port=8082");
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();
        server.registerMBean(adapter, adapterName);
        adapter.start();
        System.out.println("start.....");
    }
}

