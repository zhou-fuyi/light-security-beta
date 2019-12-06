package com.light.security.core.access.model.base;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName BaseEntity
 * @Description 所有实体基类
 * @Author ZhouJian
 * @Date 2019-12-06
 */
public abstract class BaseEntity implements Serializable {

    protected Integer id;
    protected Date createTime;
    protected Date updateTime;

    public BaseEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 顶层Builder接口, 用于构建{@link BaseEntity}类型
     */
    private interface BaseBuilder {

        <T extends BaseBuilder> T createTime(Date createTime);

        <T extends BaseBuilder> T updateTime(Date updateTime);

        <T extends BaseEntity> T build();
    }

    /**
     * 基础默认情况实现
     */
    protected static abstract class AbstractBaseBuilder implements BaseBuilder{

        private Integer id;//毕传参数

        private Date createTime;
        private Date updateTime;

        public Integer getId() {
            return id;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        protected AbstractBaseBuilder(final Integer id){
            Assert.notNull(id, "构造器不接受空值参数 -- id is null");
            this.id = id;
        }

        @Override
        public <T extends BaseBuilder> T createTime(Date createTime) {
            this.createTime = createTime;
            return (T) this;
        }

        @Override
        public <T extends BaseBuilder> T updateTime(Date updateTime) {
            this.updateTime = updateTime;
            return (T) this;
        }
    }
}
