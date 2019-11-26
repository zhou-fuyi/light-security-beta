package com.light.security.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.constant.AuthenticationConstant;
import com.light.security.core.exception.AccessDeniedException;
import com.light.security.core.exception.AuthenticationException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @ClassName ServletUtils
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public class ServletUtils {

    private static ObjectMapper objectMapper;
    private static String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    static {
        /**
         * 处理ObjectMapper序列化时时间格式
         */
        objectMapper = new Jackson2ObjectMapperBuilder().findModulesViaServiceLoader(true)
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN)))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN)))
                .build();
    }
    private static Logger logger = LoggerFactory.getLogger(ServletUtils.class);
    private static int DEFAULT_STATUS = 200;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";

    /**
     * 获取API路径
     * @param request
     * @return
     */
    public static String getServletPath(HttpServletRequest request){
        Assert.notNull(request, "request 不能为 null");
        String servletPath = request.getServletPath();
        if (StringUtils.isEmpty(servletPath)){
            logger.warn("请求都到这里了, 怎么会没有啊, servletPath is {}", servletPath);
            throw new RuntimeException("请求地址为空, 服务异常");
        }
        if (logger.isDebugEnabled()){
            logger.debug("当前请求的API地址：{}", servletPath);
        }
        if (request.getPathInfo() != null){
            servletPath += request.getPathInfo();
        }
        return servletPath;
    }


    /**
     * 适应特定场景的数据回写
     * @param request
     * @param response
     * @param map
     * @throws IOException
     */
    public static void writeMap(HttpServletRequest request, HttpServletResponse response, Map<String, Object> map) throws IOException {
        Integer status = null;
        String message = null;
        try {
            status = (Integer) map.get(KEY_STATUS);
            message = (String) map.get(KEY_MESSAGE);

        }catch (ClassCastException e){
            logger.error("writeMap方法内部出现类型转换异常, 请进行检查：{}", e);
        }
        if (status == null){
            status = DEFAULT_STATUS;
            if (logger.isWarnEnabled()){
                logger.warn("当前未设置返回状态码, 已启用默认状态码：status is {}", status);
            }
        }

        if (message == null){
            if (logger.isWarnEnabled()){
                logger.warn("当前未设置返回消息：message is null");
            }
        }
        response.setStatus(status);
        map.remove(KEY_STATUS);
        map.remove(KEY_MESSAGE);
        write(response, message, map);
    }

    /**
     * 认证成功后回写
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     */
    public static void writeAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setStatus(DEFAULT_STATUS);
        //todo 这里需要处理一下authentication
        write(response, AuthenticationConstant.AUTHENTICATION_SUCCESS_MASSAGE, authentication);
    }

    public static void writeAuthenticationException(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setStatus(exception.getCode());
        writeException(request, response, exception);
    }

    public static void writeAccessDeniedException(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {
        response.setStatus(exception.getCode());
        writeException(request, response, exception);
    }

    /**
     * 出现异常回写
     * @param request
     * @param response
     * @param exception
     * @throws IOException
     */
    public static void writeException(HttpServletRequest request, HttpServletResponse response, RuntimeException exception) throws IOException {
        write(response, exception.getMessage(), null);
    }

    /**
     * 通过HttpServletResponse的输出流写出数据
     * @param response
     * @param msg
     * @param object
     * @throws IOException
     */
    private static void write(HttpServletResponse response,String msg,  Object object) throws IOException {
        if (logger.isDebugEnabled()){
            logger.debug("由Servlet进行数据返回");
        }
        response.setContentType("application/json;charset=UTF-8");
        Result result = new Result(response.getStatus(), msg, object);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    /**
     * 返回结果封装
     */
    private static class Result {

        private int status;
        private String msg;
        private LocalDateTime current_time;
        private Object data;

        public Result(){}

        public Result(int status, String msg, Object data){
            this.status = status;
            this.msg = msg;
            this.current_time = LocalDateTime.now();
            this.data = data;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public LocalDateTime getCurrent_time() {
            return current_time;
        }

        public void setCurrent_time(LocalDateTime current_time) {
            this.current_time = current_time;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
