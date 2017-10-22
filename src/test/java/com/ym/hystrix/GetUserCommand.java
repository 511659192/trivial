package com.ym.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by ym on 2017/10/16.
 */
public class GetUserCommand extends HystrixCommand<UserDTO>  {

    private static Logger logger = LoggerFactory.getLogger(GetUserCommand.class);

    private RestTemplate restTemplate;
    private Long id;

    /**
     * 构造函数，注入配置、用到的资源访问类和命令参数.
     */
    protected GetUserCommand(HystrixCommand.Setter setter, RestTemplate restTemplate, Long id) {
        super(setter);
        this.restTemplate = restTemplate;
        this.id = id;
    }

    /**
     * 访问依赖资源的函数的实现。
     */
    @Override
    protected UserDTO run() throws Exception {
        try {
//            TimeUnit.SECONDS.sleep(1);
            logger.info("Access restful resource");
//			return restTemplate.getForObject("http://wow.duowan.com", UserDTO.class, id);
            UserDTO dto = new UserDTO();
            dto.setEmail("cdyangmeng@jd.com");
            dto.setId(111L);
            dto.setLoginName("yangmeng");
            dto.setName("yangmeng");
            dto.setTeamId(100L);
            return dto;
        } catch (HttpStatusCodeException e) {
            throw handleException(e);
        }
    }

    /**
     * 处理异常，对于客户端自己的异常，抛出HystrixBadRequestException，不算入短路统计内。
     */
    protected Exception handleException(HttpStatusCodeException e) {
        HttpStatus status = e.getStatusCode();
        if (status.equals(HttpStatus.BAD_REQUEST)) {
            throw new HystrixBadRequestException(e.getResponseBodyAsString(), e);
        }
        throw e;
    }

    @Override
    protected UserDTO getFallback() {
        UserDTO dto = new UserDTO();
        dto.setEmail("cdyangmeng2@jd.com");
        dto.setId(222L);
        dto.setLoginName("yangmeng2");
        dto.setName("yangmeng2");
        dto.setTeamId(200L);
        return dto;
    }

    @Override
    protected String getCacheKey() {
        return super.getCacheKey();
    }
}
