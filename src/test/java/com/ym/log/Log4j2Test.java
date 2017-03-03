package com.ym.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author yangmeng44
 * @Date 2017/3/3
 */
public class Log4j2Test {

    private static Logger logger = LoggerFactory.getLogger(Log4j2Test.class);

    public static void main(String[] args) {
        logger.error("afaefafe");
    }
}
