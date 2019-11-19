package com.light.security.exception;

/**
 * @ClassName LightSecurityException
 * @Description LightSecurity内异常顶层类, 其下大体分为<code>AuthenticationException</code>和<code>AuthorizationException</code>
 * @Author ZhouJian
 * @Date 2019-11-18
 */
public class LightSecurityException extends RuntimeException{

    private Integer code;

    public LightSecurityException(Integer code, String msg){
        super(msg);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
