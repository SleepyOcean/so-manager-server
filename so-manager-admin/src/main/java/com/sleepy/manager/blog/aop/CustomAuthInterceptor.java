package com.sleepy.manager.blog.aop;

import com.sleepy.manager.blog.common.UnionResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 自定义权限校验
 *
 * @author gehoubao
 * @create 2021-11-04 18:44
 **/
@Aspect
@Component
@Slf4j
public class CustomAuthInterceptor {

    private static final String COOKIE_KEY_T = "urs_t";
    private static final String COOKIE_KEY_U = "urs_u";
    private static final String COOKIE_KEY_TYPE = "urs_type";
    private static final String COOKIE_KEY_UID = "wyy_uid";

//    @Value("${application.netease.auth-url}")
    private String authUrl;

    @Pointcut("execution(public com.sleepy.manager.blog.common.UnionResponse* *WithAuth(..))")
    public void authPointCut() {
    }

    @Around("authPointCut() &&args(..)")
    public UnionResponse around(ProceedingJoinPoint point) {
        try {
            // build auth list request
            // HttpServletRequest request = ServletUtils.getRequest();
            // Cookie[] cookies = request.getCookies();
            // AssembledData.Builder builder = new AssembledData.Builder();
            // if (StringUtils.isEmpty(request.getHeader("resources"))) {
            //     return new UnionResponse.Builder().status(HttpStatus.FORBIDDEN).message("Please provide 'resources' in request header!").build();
            // }
            // Arrays.asList(cookies).stream().forEach(cookie -> {
            //     if (COOKIE_KEY_T.equals(cookie.getName())) {
            //         builder.put("t", cookie.getValue());
            //     } else if (COOKIE_KEY_U.equals(cookie.getName())) {
            //         builder.put("u", cookie.getValue());
            //     } else if (COOKIE_KEY_TYPE.equals(cookie.getName())) {
            //         builder.put("type", cookie.getValue());
            //     } else if (COOKIE_KEY_UID.equals(cookie.getName())) {
            //         builder.put("wyy_uid", cookie.getValue());
            //     }
            // });
            // builder.put("ip", "127.0.0.1");
            // builder.put("product", "NEWS_PUBLICITY");
            // builder.put("resources", JSONObject.parseObject(request.getHeader("resources")));
            // AssembledData authRequest = builder.build();

            // // send auth list request
            // RestTemplate client = new RestTemplate();
            // ResponseEntity<AssembledData> responseEntity = client.postForEntity(authUrl, authRequest, AssembledData.class);
            // // failed to obtain auth list from netease backend, return retry message
            // if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            //     return new UnionResponse.Builder()
            //             .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).message("Failed to get auth info, please try again!")
            //             .data(responseEntity.getBody())
            //             .build();
            // }
            // // user without api authorization, return UNAUTHORIZED
            // JSONObject resources = responseEntity.getBody().getJSONObject("authAccess");
            // if (null == resources || !resources.getBoolean(resources.keySet().stream().toArray()[0].toString())) {
            //     return new UnionResponse.Builder().status(HttpStatus.UNAUTHORIZED).build();
            // }
            // user with right authorization, api proceed
            return (UnionResponse) point.proceed();
        } catch (Throwable e) {
            return new UnionResponse.Builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }
}