package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.exception.ProcessorNotFoundException;
import com.light.security.core.exception.SubjectNameNotFoundException;
import com.light.security.core.properties.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @ClassName JdbcDaoImpl
 * @Description 仿照SpringSecurity中JdbcDaoImpl完成
 *
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public class JdbcDaoProcessorManager implements SubjectDetailService, InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(JdbcDaoProcessorManager.class);

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private List<JdbcDaoProcessor> jdbcDaoProcessors;

    private JdbcDaoProcessor currentProcessor;

    private boolean autoCreateTableOnStartup;

    @Override
    public SubjectDetail loadSubjectBySubjectName(String subjectName) throws SubjectNameNotFoundException {
        Enum currentAuthType = securityProperties.getAuthType();
        RuntimeException lastException = null;
        SubjectDetail subject = null;
        if (currentProcessor == null){
            for (JdbcDaoProcessor processor : jdbcDaoProcessors){
                if (!processor.support(currentAuthType)){
                    continue;
                }
                if (logger.isDebugEnabled()){
                    logger.debug("开始执行 processor: {} #loadSubjectBySubjectName(subjectName)", processor.getClass().getSimpleName());
                }

                try {
                    subject = processor.loadSubjectBySubjectName(subjectName);
                    if (subject != null){
                        break;
                    }
                }catch (SubjectNameNotFoundException ex){
                    if (logger.isDebugEnabled()){
                        logger.debug("异常: {}", ex.getMessage());
                    }
                    throw ex;
                }catch (RuntimeException ex){
                    lastException = ex;
                }
            }
        }else {
            try {
                subject = currentProcessor.loadSubjectBySubjectName(subjectName);
            }catch (SubjectNameNotFoundException ex){
                if (logger.isDebugEnabled()){
                    logger.debug("异常: {}", ex.getMessage());
                }
                throw ex;
            }catch (RuntimeException ex){
                lastException = ex;
            }
        }
        if (subject != null){
            return subject;
        }

        if (lastException == null){
            lastException = new ProcessorNotFoundException(500, "找不到适应当前 CurrentAuthType 的 JdbcDaoProcessor");
        }

        throw lastException;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(securityProperties, "securityProperties 不能为null");
        Assert.notNull(jdbcDaoProcessors, "jdbcDaoProcessors 不能为null");
        /**
         * 是否在启动的时候自动创建表
         */
        if (autoCreateTableOnStartup){
            Enum currentAuthType = securityProperties.getAuthType();
            for (JdbcDaoProcessor processor : jdbcDaoProcessors){
                if (processor.support(currentAuthType)){
                    this.currentProcessor = processor;
                    processor.autoInitTable();
                    break;
                }
            }
        }
    }

    public boolean isAutoCreateTableOnStartup() {
        return autoCreateTableOnStartup;
    }

    public void setAutoCreateTableOnStartup(boolean autoCreateTableOnStartup) {
        this.autoCreateTableOnStartup = autoCreateTableOnStartup;
    }
}
