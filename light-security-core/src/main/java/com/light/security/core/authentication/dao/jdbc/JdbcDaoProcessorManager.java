package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.exception.InternalServiceException;
import com.light.security.core.exception.ProcessorNotFoundException;
import com.light.security.core.exception.SubjectNameNotFoundException;
import com.light.security.core.properties.SecurityProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @ClassName JdbcDaoProcessorManager
 * @Description {@link DaoProcessorManager}的默认实现, 用于调度{@link JdbcDaoProcessor}完成账户主体的认证
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public class JdbcDaoProcessorManager implements DaoProcessorManager {

    private final static Logger logger = LoggerFactory.getLogger(JdbcDaoProcessorManager.class);

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 依赖搜索, 自动注入{@link JdbcDaoProcessor}的实例类
     */
    @Autowired
    private List<JdbcDaoProcessor> jdbcDaoProcessors;

    /**
     * 原本是用于优化操作的, 但是目前我的代码实现不好看, 不优雅了, 待我后续改造
     */
    private JdbcDaoProcessor currentProcessor;

    /**
     * 是否在系统启动的时候自动创建表, 默认为false
     */
    private boolean autoCreateTableOnStartup;

    public JdbcDaoProcessor getCurrentProcessor() {
        return currentProcessor;
    }

    public void setCurrentProcessor(JdbcDaoProcessor currentProcessor) {
        this.currentProcessor = currentProcessor;
    }

    // TODO: 2019-12-10 代码需要改造
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
                    lastException = ex;
                }catch (InternalServiceException ex){
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
            }catch (InternalServiceException ex){
                if (logger.isDebugEnabled()){
                    logger.debug("异常: {}", ex.getMessage());
                }
                lastException = ex;
            }catch (RuntimeException ex){
                lastException = ex;
            }
        }
        if (subject != null){
            return subject;
        }

        if (lastException == null){
            lastException = new ProcessorNotFoundException(500, "找不到适应当前 CurrentAuthType --> " + currentAuthType + " 的 JdbcDaoProcessor");
        }

        throw lastException;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info(ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE));
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
                    processor.autoInitTable(currentAuthType);
                    return;
                }
            }
            if (logger.isWarnEnabled()){
                logger.warn("未找到适配当前模式的JdbcDaoProcessor处理器, 请确认是否支持模式: {}", currentAuthType.name());
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
