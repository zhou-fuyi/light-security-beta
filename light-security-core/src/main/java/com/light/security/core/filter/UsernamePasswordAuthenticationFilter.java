package com.light.security.core.filter;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.authentication.token.UsernamePasswordAuthenticationToken;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.exception.AuthenticationServiceException;
import com.light.security.core.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UsernamePasswordAuthenticationFilter
 * @Description 根据账户主体名称与密码进行认证
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationFilter {

    private static final String PROCESS_URL = "/login";
    private static final String DEFAULT_HTTP_METHOD = "POST";
    public static final String LIGHT_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String LIGHT_SECURITY_FORM_PASSWORD_KEY = "password";

    private String usernameParameter = LIGHT_SECURITY_FORM_USERNAME_KEY;
    private String passwordParameter = LIGHT_SECURITY_FORM_PASSWORD_KEY;
    private boolean postOnly = true;
    
    public UsernamePasswordAuthenticationFilter(){
        super(new AntPathRequestMatcher(PROCESS_URL, DEFAULT_HTTP_METHOD));
    }

    @Override
    protected Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        if (postOnly && !request.getMethod().equals("POST")){
            throw new AuthenticationServiceException(400, "不支持当前身份认证方法: " + request.getMethod());
        }
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        if (username == null){
            username = "";
        }
        if (password == null){
            password = "";
        }
        username = username.trim();//密码存在前后空格也为有效字符的情况, 但是用户名一般不存在
        UsernamePasswordAuthenticationToken requestToken = new UsernamePasswordAuthenticationToken(username, password);

        //对于Details的构建, 可以通过父类的<code>setAuthenticationDetailsSource()</code>方法重新指定Details数据构建源, 也可以通过重写setDetails()方法实现
        setDetail(request, requestToken);
        return getAuthenticationManager().authenticate(requestToken);
    }

    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(usernameParameter);
    }

    protected String obtainPassword(HttpServletRequest request){
        return request.getParameter(passwordParameter);
    }

    protected void setDetail(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest){
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    public boolean isPostOnly() {
        return postOnly;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }
}
