package soma.test.waggle.logging;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import soma.test.waggle.util.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * logging을 위한 AOP
 * */
@Component
@Aspect
@Slf4j
public class LogAspect {

    /**
     * 모든 controller에 적용
     * log 내역
     *      REQUEST | MemberId: {memberId} |URI: {request uri} | Method: {HTTP Method} | {} = {Args based on params}
     *      RESPONSE | URI: {request uri} | Method: {HTTP Method} | {Controller.method} = {return result} ({걸린 시간}ms)
     * */
//    @Around("execution(* soma.test.waggle.controller..*.*(..)")
    @Around("within(soma.test.waggle.controller..*)")
    public Object logging(ProceedingJoinPoint pjp) throws Throwable {
        Map<String, String> params = getRequestParams();

        long startAt = System.currentTimeMillis();

        //params.get("params")
        log.info("----------> REQUEST | MemberId: {} |URI: {} | Method: {} | {} = {} ",
                SecurityUtil.getCurrentMemberId() ,
                params.get("URI"),
                params.get("Method"),
                pjp.getSignature().toShortString(),
                pjp.getArgs());

        Object result = pjp.proceed();

        long endAt = System.currentTimeMillis();
        // pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName()
        log.info("----------> RESPONSE | URI: {} | Method: {} | {} = {} ({}ms)",
                params.get("URI"),
                params.get("Method"),
                pjp.getSignature().toShortString(),
                result,
                endAt-startAt);

        return result;

    }

    // get request value
    private Map<String, String> getRequestParams() {

        String params = "";
        String URI = "";
        String method = "";
        RequestAttributes requestAttribute = RequestContextHolder.getRequestAttributes();
        Map<String, String> returnMap = new HashMap<>();
        if(requestAttribute != null){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();
            URI = request.getRequestURI();
            method = request.getMethod();
            Map<String, String[]> paramMap = request.getParameterMap();

            if(!paramMap.isEmpty()) {
                params = " [" + paramMapToString(paramMap) + "]";
            }
        }
        returnMap.put("params", params);
        returnMap.put("URI", URI);
        returnMap.put("Method", method);
        return returnMap;
    }

    private String paramMapToString(Map<String, String[]> paramMap) {
        return paramMap.entrySet().stream()
                .map(entry -> String.format("%s : (%s)",
                        entry.getKey(), Joiner.on(",").join(entry.getValue())))
                .collect(Collectors.joining(", "));
    }
}
