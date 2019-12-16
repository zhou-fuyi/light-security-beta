package com.light.security.core.authentication.dao;

import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.authentication.token.SubjectNamePasswordAuthenticationToken;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.exception.BadCredentialsException;
import com.light.security.core.exception.InternalAuthenticationServiceException;
import com.light.security.core.exception.SubjectNameNotFoundException;
import com.light.security.core.util.crypto.bcrypt.BCryptPasswordEncoder;
import com.light.security.core.util.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @ClassName DaoAuthenticationProvider
 * @Description DaoAuthenticationProvider
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class DaoAuthenticationProvider extends AbstractSubjectDetailAuthenticationProvider {

    private static final String SUBJECT_NOT_FOUND_PASSWORD = "subject_not_found_password";

    private PasswordEncoder passwordEncoder;

//    private String subjectNotFoundPassword;

    private SubjectDetailService subjectDetailService;

    public DaoAuthenticationProvider(){
        setPasswordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void additionalAuthenticationChecks(SubjectDetail detail, SubjectNamePasswordAuthenticationToken authenticationToken) throws AuthenticationException {
        if (StringUtils.isEmpty(authenticationToken.getCredentials())){
            logger.debug("身份验证失败: 未提供凭据");
            throw new BadCredentialsException(400, "未提供凭证");
        }
        String presentedPassword = authenticationToken.getCredentials().toString();//账户请求认证时携带的凭证
        if (!this.passwordEncoder.matches(presentedPassword, detail.getPassword())){
            logger.debug("身份验证失败");
            throw new BadCredentialsException(401, "输入凭证错误");
        }
    }

    @Override
    protected void doAfterPropertiesSet() {
        Assert.notNull(subjectDetailService, "SubjectDetailService 不能为null");
        Assert.notNull(passwordEncoder, "PasswordEncoder 不能为null");
    }

    @Override
    protected SubjectDetail retrieveSubject(String subjectName, SubjectNamePasswordAuthenticationToken authenticationToken) throws AuthenticationException {
        SubjectDetail loadSubject = null;
        try {
            loadSubject = this.subjectDetailService.loadSubjectBySubjectName(subjectName);
        }catch (SubjectNameNotFoundException ex){
            if (logger.isDebugEnabled()){
                logger.debug("Subject {} not found", subjectName);
            }
            throw ex;
        }catch (Exception ex){
            throw new InternalAuthenticationServiceException(500, ex.getMessage());
        }

        if (loadSubject == null){
            throw new InternalAuthenticationServiceException(500, "不符合接口规定, loadSubjectBySubjectName 接口不能 return null 值");
        }
        return loadSubject;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder){
        Assert.notNull(passwordEncoder, "PasswordEncoder 不能为null");
//        this.subjectNotFoundPassword = passwordEncoder.encode(SUBJECT_NOT_FOUND_PASSWORD);
        this.passwordEncoder = passwordEncoder;
    }

    protected PasswordEncoder getPasswordEncoder(){
        return this.passwordEncoder;
    }


    public void setSubjectDetailService(SubjectDetailService subjectDetailService) {
        this.subjectDetailService = subjectDetailService;
    }

    public SubjectDetailService getSubjectDetailService() {
        return subjectDetailService;
    }
}
