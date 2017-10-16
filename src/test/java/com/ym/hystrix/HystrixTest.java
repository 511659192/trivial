package com.ym.hystrix;

import com.netflix.hystrix.*;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Created by ym on 2017/10/16.
 */
public class HystrixTest {

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        HystrixCommand.Setter commandConfig;
        // 设置Command名称 //

        commandConfig = HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")).andCommandKey(
                HystrixCommandKey.Factory.asKey("GetUserCommand"));
        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
        commandConfig.andCommandPropertiesDefaults(commandProperties);

        commandProperties.withExecutionTimeoutEnabled(false);
        // 设置短路规则 //
        // 设置短路后的保护时间 ，默认为5秒，改为20秒方便演示
        commandProperties.withCircuitBreakerSleepWindowInMilliseconds(20000)
                // 多少百分比的失败在rolling windows内发生，计算为短路。默认为50%，无改变.
                .withCircuitBreakerErrorThresholdPercentage(50)
                // 至少多少请求在rolling window内发生，才开始触发短路的计算，默认为20, 设为3方便演示.
                .withCircuitBreakerRequestVolumeThreshold(3)
                // rolling windows 长度，默认为20秒，改为120秒方便演示。同时相应改变桶的数量.
                .withMetricsRollingStatisticalWindowInMilliseconds(1000 * 60 * 10)
                .withMetricsRollingStatisticalWindowBuckets(10);

        // 设置超时与并发控制 //
        if (false) { // 使用隔离的Hystrix线程池
            // 线程超时，默认为1秒，设为3秒方便演示.
            commandProperties.withExecutionIsolationThreadTimeoutInMilliseconds(300000);
            // 线程池属性， 线程池大小，默认为10，无改变。待执行队列的大小，默认为5，无改变.
            commandConfig.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(10)
                    .withQueueSizeRejectionThreshold(5));
        } else { // 使用原有的调用者线程
            // 依靠RestTemplate本身的超时机制，设为10秒。
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(10000);

            // 设置使用原有的调用者线程，设置并发，默认为10，无改变.
            commandProperties.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                    .withExecutionIsolationSemaphoreMaxConcurrentRequests(10);
        }
        GetUserCommand command = new GetUserCommand(commandConfig, restTemplate, 1L);
        UserDTO dto = command.execute();
        System.out.println("---------------------");
        System.out.println(dto.getName());
    }
}
